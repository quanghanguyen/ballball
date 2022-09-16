package com.example.ballball.main.match.wait.details

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
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityWaitDetailsBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.databinding.SuccessDialogBinding
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.map.MapsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.clientUID
import com.example.ballball.utils.Model.confirmUID
import com.example.ballball.utils.Model.currentAddress
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import com.example.ballball.utils.Model.destinationAddress
import com.example.ballball.utils.Model.destinationLat
import com.example.ballball.utils.Model.destinationLong
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchID
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.Model.teamPhone
import com.example.ballball.utils.Model.userImageUrl
import com.example.ballball.utils.StorageConnection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WaitDetailsActivity : AppCompatActivity() {

    private lateinit var waitDetailsBinding : ActivityWaitDetailsBinding
    private val waitDetailsViewModel : WaitDetailsViewModel by viewModels()
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionId = 5
    private var currentClick : Int? = null

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, WaitDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitDetailsBinding = ActivityWaitDetailsBinding.inflate(layoutInflater)
        setContentView(waitDetailsBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        binding()
        back()
        cancelWaitRequest()
        handleVariables()
        openMap()
        phoneCall()
        chat()
    }

    private fun chat() {
        waitDetailsBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("teamName", clientTeamName)
            intent.putExtra("userUid", clientUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun phoneCall() {
        waitDetailsBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    1)
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$teamPhone"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun initObserve() {
        cancelWaitRequestObserve()
        cancelWaitListNotificationObserve()
    }

    private fun cancelWaitListNotificationObserve() {
        waitDetailsViewModel.cancelWaitMatchListNotification.observe(this) {result ->
            when (result) {
                is WaitDetailsViewModel.CancelWaitMatchListNotification.ResultOk -> {}
                is WaitDetailsViewModel.CancelWaitMatchListNotification.ResultError -> {}
            }
        }
    }

    private fun handleVariables() {
        StorageConnection.storageReference.getReference("Users").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                userImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }

        FirebaseDatabase.getInstance().getReference("Request_Match").child(matchID!!).get()
            .addOnSuccessListener {
                currentClick = it.child("click").value.toString().toInt()
            }
    }

    private fun cancelWaitRequestObserve() {
        waitDetailsViewModel.cancelWaitMatch.observe(this) {result ->
            when (result) {
                is WaitDetailsViewModel.CancelWaitMatch.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Yêu cầu bắt trận của bạn đã được hủy"
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
                is WaitDetailsViewModel.CancelWaitMatch.ResultError -> {}
                is WaitDetailsViewModel.CancelWaitMatch.NotificationOk -> {}
                is WaitDetailsViewModel.CancelWaitMatch.NotificationError -> {}
                is WaitDetailsViewModel.CancelWaitMatch.DeleteConfirmOk -> {}
                is WaitDetailsViewModel.CancelWaitMatch.DeleteConfirmError -> {}
                is WaitDetailsViewModel.CancelWaitMatch.UpdateClickOk -> {}
                is WaitDetailsViewModel.CancelWaitMatch.UpdateClickError -> {}
            }
        }
    }

    private fun cancelWaitRequest() {
        waitDetailsBinding.cancelRequest.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Hủy yêu cầu"
            signOutDialogBinding.content.text = "Bạn có muốn hủy yêu cầu bắt trận với $teamName không?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    val updatedClick = currentClick?.minus(1)
                    waitDetailsViewModel.cancelWaitMatch(userUID, matchID!!, matchDate!!, matchTime!!, clientTeamName!!, confirmUID!!, updatedClick!!)

                    val timeUtils : Long = System.currentTimeMillis()
                    waitDetailsViewModel.cancelWaitMatchListNotification(clientUID!!, clientTeamName!!, userImageUrl!!, "cancelWaitMatch",
                    matchDate!!, matchTime!!, timeUtils)
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
        waitDetailsBinding.back.setOnClickListener {
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
            with(waitDetailsBinding) {
                if (data?.click == 0) {
                    clickLayout.visibility = View.GONE
                }
                teamName.text = data?.teamName
                Glide.with(teamImage).load(data?.teamImageUrl).centerCrop().into(teamImage)
                if (data?.click  == 1) {
                    clickLayout.visibility = View.GONE
                } else {
                    val click = data?.click?.minus(1)
                    clickNumber.text = click.toString()
                }
                clickNumber.text = data?.click.toString()
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
                matchID = data?.matchID
                Model.teamName = data?.teamName
                matchDate = data?.date
                matchTime = data?.time
                teamPhone = data?.teamPhone
                clientUID = data?.clientUID
                confirmUID = data?.confirmUID
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                clientTeamName = data?.teamName
            }
        }
    }

    private fun openMap() {
        waitDetailsBinding.navigationLayout.setOnClickListener {
            if (checkPermissions()) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("currentLat", currentLat)
                intent.putExtra("currentLong", currentLong)
                intent.putExtra("currentAddress", currentAddress)
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
                        currentLat = list[0].latitude
                        currentLong = list[0].longitude
                        currentAddress = list[0].getAddressLine(0)
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