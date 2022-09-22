package com.example.ballball.main.match.upcoming.details

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
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.*
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.main.match.confirm.details.ConfirmDetailsActivity
import com.example.ballball.map.MapsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.clientPhone
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.clientUID
import com.example.ballball.utils.Model.destinationAddress
import com.example.ballball.utils.Model.destinationLat
import com.example.ballball.utils.Model.destinationLong
import com.example.ballball.utils.Model.deviceToken
import com.example.ballball.utils.Model.geoHash
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
import com.example.ballball.utils.Model.uid
import com.example.ballball.utils.Model.userImageUrl
import com.example.ballball.utils.StorageConnection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UpComingDetailsActivity : AppCompatActivity() {

    private lateinit var upComingDetailsBinding: ActivityUpComingDetailsBinding
    private val upComingDetailsViewModel : UpComingDetailsViewModel by viewModels()
    private lateinit var cancelMatchDialogBinding: CancelMatchDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var successDialogBinding: SuccessDialogBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionId = 10

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, UpComingDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upComingDetailsBinding = ActivityUpComingDetailsBinding.inflate(layoutInflater)
        setContentView(upComingDetailsBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        binding()
        handleVariable()
        back()
        cancelMatch()
        openMap()
        phoneCall()
        chat()
    }

    private fun handleVariable() {
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID!!).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }

        FirebaseDatabase.getInstance().getReference("Users").child(clientUID!!).get()
            .addOnSuccessListener {
                clientPhone = it.child("userPhone").value.toString()
            }

        StorageConnection.storageReference.getReference("Users").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                userImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
        }

    private fun phoneCall() {
        upComingDetailsBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    1)
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${clientPhone}"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun chat() {
        upComingDetailsBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("teamName", clientTeamName)
            intent.putExtra("userUid", clientUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun initObserve() {
        cancelUpComingMatchObserve()
        cancelUpComingListNotification()
        restoreMatchObserve()
    }

    private fun restoreMatchObserve() {
        upComingDetailsViewModel.restoreMatch.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.RestoreMatch.ResultOk -> {}
                is UpComingDetailsViewModel.RestoreMatch.ResultError -> {}
            }
        }
    }

    private fun cancelUpComingListNotification() {
        upComingDetailsViewModel.cancelUpComingListNotification.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.CancelUpComingListNotification.ResultOk -> {}
                is UpComingDetailsViewModel.CancelUpComingListNotification.ResultError -> {}
            }
        }
    }

    private fun cancelMatch() {
        upComingDetailsBinding.cancelMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            cancelMatchDialogBinding = CancelMatchDialogBinding.inflate(layoutInflater)
            dialog.setContentView(cancelMatchDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            cancelMatchDialogBinding.yes.setOnClickListener {
                val id : Int = cancelMatchDialogBinding.radioGroup.checkedRadioButtonId
                if (id != -1) {
                    val radio : RadioButton = cancelMatchDialogBinding.root.findViewById(id)
                    val radioText = radio.text.toString()
                    if (userUID != null) {
                        upComingDetailsViewModel.cancelUpComingMatch(clientUID!!, userUID, matchID!!, matchDate!!, matchTime!!,
                        teamName!!, radioText)

                        upComingDetailsViewModel.restoreMatch(uid!!, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!, matchTime!!,
                            matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!, lat!!, long!!, geoHash!!)
                    }
                    val timeUtils : Long = System.currentTimeMillis()
                    upComingDetailsViewModel.cancelUpComingListNotification(clientUID!!, teamName!!, userImageUrl!!, "cancelUpComing", matchDate!!, matchTime!!, radioText, timeUtils)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Vui lòng chọn lí do", Toast.LENGTH_SHORT).show()
                }
            }
            cancelMatchDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun cancelUpComingMatchObserve() {
        upComingDetailsViewModel.cancelUpComing.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.CancelUpComing.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Trận đấu này đã được hủy"
                    successDialogBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        dialog.cancel()
                        finish()
                        Animation.animateSlideRight(this)
                    }, 5000)
                }
                is UpComingDetailsViewModel.CancelUpComing.ResultError -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationOk -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationError -> {}
            }
        }
    }

    private fun back() {
        upComingDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(upComingDetailsBinding) {
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                if (data?.note?.isEmpty() == true) {
                    note.text = "..."
                } else {
                    note.text = data?.note
                }

                if (userUID == data?.userUID) {
                    data?.clientUID?.let { path ->
                        FirebaseDatabase.getInstance().getReference("Teams").child(path).get()
                            .addOnSuccessListener {
                                val image = it.child("teamImageUrl").value.toString()
                                Glide.with(teamImage).load(image).centerCrop().into(teamImage)
                            }
                        }
                    teamName.text = data?.clientTeamName
                } else {
                    data?.userUID?.let { path ->
                        FirebaseDatabase.getInstance().getReference("Teams").child(path).get()
                            .addOnSuccessListener {
                                val image = it.child("teamImageUrl").value.toString()
                                Glide.with(teamImage).load(image).centerCrop().into(teamImage)
                            }
                    }
                    teamName.text = data?.teamName
                }

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
                Model.click = data?.click
                clientTeamName = data?.clientTeamName
                Model.clientImageUrl = data?.clientImageUrl
                clientUID = data?.clientUID
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                uid = data?.userUID
                geoHash = data?.geoHash
            }
        }
    }

    private fun openMap() {
        upComingDetailsBinding.navigationLayout.setOnClickListener {
            if (checkPermissions()) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("currentLat", Model.currentLat)
                intent.putExtra("currentLong", Model.currentLong)
                intent.putExtra("currentAddress", Model.currentAddress)
                intent.putExtra("destinationLat", destinationLat)
                intent.putExtra("destinationLong", destinationLong)
                intent.putExtra("destinationAddress", destinationAddress)
                startActivity(intent)
                Animation.animateSlideLeft(this)
            } else {
                requestPermissions()
            }
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
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        Model.currentLat = list[0].latitude
                        Model.currentLong = list[0].longitude
                        Model.currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", Model.currentAddress.toString())
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