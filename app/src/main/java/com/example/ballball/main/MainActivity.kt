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
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.ballball.R
import com.example.ballball.creatematch.CreateMatchActivity
import com.example.ballball.databinding.ActivityMainBinding
import com.example.ballball.databinding.LocationAccessDialogBinding
import com.example.ballball.utils.Animation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 0
    private lateinit var locationAccessDialogBinding: LocationAccessDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
    }

    private fun initEvents() {
        navBinding()
        locationRequest()
        createMatch()
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
                        Log.e("Latitude", list[0].latitude.toString())
                        Log.e("Latitude", list[0].longitude.toString())
//                        mainBinding.apply {
//                            tvLatitude.text = "Latitude\n${list[0].latitude}"
//                            tvLongitude.text = "Longitude\n${list[0].longitude}"
//                            tvCountryName.text = "Country Name\n${list[0].countryName}"
//                            tvLocality.text = "Locality\n${list[0].locality}"
//                            tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
//                        }
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