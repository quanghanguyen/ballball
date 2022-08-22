package com.example.ballball.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import ch.hsr.geohash.GeoHash.fromGeohashString
import com.example.ballball.R
import com.example.ballball.creatematch.CreateMatchActivity
import com.example.ballball.databinding.ActivityMainBinding
import com.example.ballball.databinding.LocationAccessDialogBinding
import com.example.ballball.main.home.nearme.NearMeFragment
import com.example.ballball.user.walkthrough.team.TeamViewModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.MessageConnection.firebaseMessaging
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.StorageConnection
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 0
    private lateinit var locationAccessDialogBinding: LocationAccessDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private var userAvatarUrl : String? = null
    private var teamAvatarUrl : String? = null
    private var teamName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firebaseMessaging.subscribeToTopic("requestMatch")
        initEvents()
        initObserves()
    }

    private fun initObserves() {
        updateUserObserve()
    }

    private fun updateUserObserve() {
        mainViewModel.updateUsers.observe(this) {result ->
            when (result) {
                is MainViewModel.UpdateUsers.ResultOk -> {}
                is MainViewModel.UpdateUsers.ResultError -> {}
            }
        }
    }

    private fun initEvents() {
        handleVariables()
        navBinding()
        locationRequest()
        createMatch()
    }

    private fun handleVariables() {
        StorageConnection.storageReference.getReference("Users").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                userAvatarUrl = it.toString()
                mainViewModel.updateUser(userUID, userAvatarUrl!!)
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }

        StorageConnection.storageReference.getReference("Teams").child(userUID).downloadUrl
            .addOnSuccessListener {
                teamAvatarUrl = it.toString()
                mainViewModel.updateTeams(userUID, teamAvatarUrl!!)
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }

        FirebaseDatabase.getInstance().getReference("Teams").child(userUID).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }
        }

    private fun createMatch() {
        mainBinding.fab.setOnClickListener {
            startActivity(Intent(this, CreateMatchActivity::class.java))
            Animation.animateInAndOut(this)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun locationRequest() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        currentLat = list[0].latitude
                        currentLong = list[0].longitude
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled (
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        val dialog = Dialog(this, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        locationAccessDialogBinding = LocationAccessDialogBinding.inflate(layoutInflater)
        dialog.setContentView(locationAccessDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        locationAccessDialogBinding.yes.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionId
            )
            dialog.dismiss()
        }
        locationAccessDialogBinding.no.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                locationRequest()
            }
        }
    }

    private fun navBinding() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(mainBinding.bottomNavigation, navController)
    }
}