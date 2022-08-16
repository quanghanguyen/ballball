package com.example.ballball.main.match.confirm.details

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
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityConfirmDetailsBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.databinding.SuccessDialogBinding
import com.example.ballball.map.MapsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.click
import com.example.ballball.utils.Model.clientImageUrl
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.clientUID
import com.example.ballball.utils.Model.confirmUID
import com.example.ballball.utils.Model.currentAddress
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import com.example.ballball.utils.Model.destinationAddress
import com.example.ballball.utils.Model.destinationLat
import com.example.ballball.utils.Model.destinationLong
import com.example.ballball.utils.Model.deviceToken
import com.example.ballball.utils.Model.lat
import com.example.ballball.utils.Model.locationAddress
import com.example.ballball.utils.Model.long
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchID
import com.example.ballball.utils.Model.matchLocation
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamImageUrl
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.Model.teamNote
import com.example.ballball.utils.Model.teamPeopleNumber
import com.example.ballball.utils.Model.teamPhone
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ConfirmDetailsActivity : AppCompatActivity() {

    private lateinit var confirmDetailsBinding : ActivityConfirmDetailsBinding
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val confirmDetailsViewModel : ConfirmDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, ConfirmDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        confirmDetailsBinding = ActivityConfirmDetailsBinding.inflate(layoutInflater)
        setContentView(confirmDetailsBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserves()
    }

    private fun initEvents() {
        binding()
        back()
        denyConfirmMatch()
        acceptMatch()
        openMap()
    }

    private fun initObserves() {
        denyConfirmMatchObserve()
        acceptMatchObserve()
        saveUpComingClientObserve()
    }

    private fun saveUpComingClientObserve() {
        confirmDetailsViewModel.saveUpComingClient.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientOk -> {}
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientError -> {}
            }
        }
    }

    private fun acceptMatchObserve() {
        confirmDetailsViewModel.acceptMatch.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Thành Công"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DenyMatchNotificationOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DenyMatchNotificationError -> {}
            }
        }
    }

    private fun acceptMatch() {
        confirmDetailsBinding.acceptRequest.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Đồng ý"
            signOutDialogBinding.content.text = "Bạn đồng ý yêu cầu bắt trận của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.acceptMatch(userUID, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!,
                        matchTime!!, matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!,
                        lat!!, long!!, click!!, clientTeamName!!, clientImageUrl!!, confirmUID!!, confirmUID!!)
                }
                if (userUID != null) {
                    confirmDetailsViewModel.saveUpComingClient(userUID, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!,
                        matchTime!!, matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!,
                        lat!!, long!!, click!!, clientTeamName!!, clientImageUrl!!, confirmUID!!, userUID)
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun denyConfirmMatchObserve() {
        confirmDetailsViewModel.denyConfirmMatch.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.DenyConfirmMatch.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Bạn đã từ chối yêu cầu của $clientTeamName"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is ConfirmDetailsViewModel.DenyConfirmMatch.ResultError -> {}
            }
        }
    }

    private fun denyConfirmMatch() {
        confirmDetailsBinding.denyMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Từ chối"
            signOutDialogBinding.content.text = "Bạn muốn từ chối yêu cầu bắt trận của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.denyConfirmMatch(userUID, matchID!!, confirmUID!!, confirmUID!!,
                        matchDate!!, matchTime!!, teamName!!)
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun back() {
        confirmDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(confirmDetailsBinding) {
                teamName.text = data?.clientTeamName
                Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                note.text = data?.note


                matchID = data?.matchID
                deviceToken = data?.deviceToken
                Model.teamName = data?.teamName
                teamPhone = data?.teamPhone
                matchDate = data?.date
                matchTime = data?.time
                matchLocation = data?.location
                teamNote = data?.note
                teamPeopleNumber = data?.teamPeopleNumber
                teamImageUrl = data?.teamImageUrl
                Model.locationAddress = data?.locationAddress
                lat = data?.lat
                long = data?.long
                click = data?.click
                clientTeamName = data?.clientTeamName
                clientImageUrl = data?.clientImageUrl
                confirmUID = data?.confirmUID
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
            }
        }
    }

    private fun openMap() {
        confirmDetailsBinding.navigationLayout.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("currentLat", currentLat)
            intent.putExtra("currentLong", currentLong)
            intent.putExtra("currentAddress", currentAddress)
            intent.putExtra("destinationLat", destinationLat)
            intent.putExtra("destinationLong", destinationLong)
            intent.putExtra("destinationAddress", destinationAddress)

            Log.e("currentLat", currentLat.toString())
            Log.e("currentLong", currentLong.toString())
            Log.e("currentAddress", currentAddress.toString())
            Log.e("destinationLat", destinationLat.toString())
            Log.e("destinationLong", destinationLong.toString())
            Log.e("destinationAddress", destinationAddress.toString())
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        currentLat = list[0].latitude
                        currentLong = list[0].longitude
                        currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", currentAddress.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Hãy bật định vị của bạn", Toast.LENGTH_LONG).show()
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
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
}