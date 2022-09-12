package com.example.ballball.main.home.all.details

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
import com.example.ballball.databinding.ActivityAllDetailsBinding
import com.example.ballball.databinding.FragmentAllBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.databinding.SuccessDialogBinding
import com.example.ballball.login.phone.login.SignInActivity
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.map.MapsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model.clientImageUrl
import com.example.ballball.utils.Model.clientPhone
import com.example.ballball.utils.Model.currentAddress
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import com.example.ballball.utils.Model.destinationAddress
import com.example.ballball.utils.Model.destinationLat
import com.example.ballball.utils.Model.destinationLong
import com.example.ballball.utils.Model.deviceToken
import com.example.ballball.utils.Model.geoHash
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchLocation
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamConfirmUID
import com.example.ballball.utils.Model.teamImageUrl
import com.example.ballball.utils.Model.teamNote
import com.example.ballball.utils.Model.teamPeopleNumber
import com.example.ballball.utils.Model.userImageUrl
import com.example.ballball.utils.StorageConnection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AllDetailsActivity : AppCompatActivity() {
    private lateinit var allDetailsBinding: ActivityAllDetailsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val allDetailsViewModel : AllDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val permissionId = 2
    var phoneNumber : String? = null
    var name : String? = null
    var click : Int? = null
    var matchID : String? = null
    var clientTeamName : String? = null

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, AllDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        allDetailsBinding = ActivityAllDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(allDetailsBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        handleVariable()
        binding()
        openMap()
        back()
        phoneCall()
        chat()
        catchMatch()
    }

    private fun handleVariable() {
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID!!).get()
            .addOnSuccessListener {
                clientTeamName = it.child("teamName").value.toString()
            }

        StorageConnection.storageReference.getReference("Teams").child(userUID).downloadUrl
            .addOnSuccessListener {
                clientImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }

        StorageConnection.storageReference.getReference("Users").child(userUID).downloadUrl
            .addOnSuccessListener {
                userImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
    }

    private fun initObserve() {
        catchMatchObserve()
        saveWaitMatchListNotificationObserve()
    }

    private fun saveWaitMatchListNotificationObserve() {
        allDetailsViewModel.waitMatchListNotification.observe(this) {result ->
            when (result) {
                is AllDetailsViewModel.WaitMatchListNotificationResult.ResultOk -> {}
                is AllDetailsViewModel.WaitMatchListNotificationResult.ResultError -> {}
            }
        }
    }

    private fun catchMatchObserve() {
        allDetailsViewModel.catchMatch.observe(this) {result ->
            when (result) {
                is AllDetailsViewModel.CatchMatch.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Yêu cầu của bạn đã được gửi, chờ $name xác nhận"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is AllDetailsViewModel.CatchMatch.ResultError -> {}
                is AllDetailsViewModel.CatchMatch.WaitMatchOk -> {}
                is AllDetailsViewModel.CatchMatch.WaitMatchError -> {}
                is AllDetailsViewModel.CatchMatch.WaitMatchNotificationOk -> {}
                is AllDetailsViewModel.CatchMatch.WaitMatchNotificationError -> {}
                is AllDetailsViewModel.CatchMatch.ConfirmMatchOk -> {}
                is AllDetailsViewModel.CatchMatch.ConfirmMatchError -> {}
            }
        }
    }

    private fun catchMatch() {
        allDetailsBinding.catchMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Bắt trận"
            signOutDialogBinding.content.text = "Bạn có muốn bắt trận với $name không?"
            signOutDialogBinding.yes.setOnClickListener {
                click = click?.plus(1)
                val clientUID = "clientUID$click"
                matchID?.let { matchID ->
                    if (userUID != null) {
                        click?.let { click ->
                            allDetailsViewModel.handleCatchMatch(userUID, userUID, teamConfirmUID!!,
                                matchID, deviceToken!!, name!!, phoneNumber!!, matchDate!!,
                                matchTime!!, matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!,
                                destinationAddress!!, destinationLat!!, destinationLong!!, click, clientTeamName!!, clientUID,
                                clientImageUrl!!, userUID, teamConfirmUID!!, geoHash!!)

                            val timeUtils : Long = System.currentTimeMillis()
                            allDetailsViewModel.waiMatchListNotification(teamConfirmUID!!, clientTeamName!!, userImageUrl!!, "waitMatch", matchDate!!, matchTime!!, timeUtils)
                        }
                    }
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun phoneCall() {
        allDetailsBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    1)
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun chat() {
        allDetailsBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("teamName", name)
            intent.putExtra("userUid", teamConfirmUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun back() {
        allDetailsBinding.back.setOnClickListener {
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
            with(allDetailsBinding) {
                if (data?.click == 0) {
                    clickLayout.visibility = View.GONE
                }
                teamName.text = data?.teamName
                Glide.with(teamImage).load(data?.teamImageUrl).centerCrop().into(teamImage)
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

                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                phoneNumber = data?.teamPhone
                name = data?.teamName
                click = data?.click
                matchID = data?.matchID
                deviceToken = data?.deviceToken
                matchDate = data?.date
                matchTime = data?.time
                teamNote = data?.note
                teamPeopleNumber = data?.teamPeopleNumber
                teamImageUrl = data?.teamImageUrl
                matchLocation = data?.location
                teamConfirmUID = data?.userUID
                geoHash = data?.geoHash
            }
        }
    }

    private fun openMap() {
        allDetailsBinding.navigationLayout.setOnClickListener {
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