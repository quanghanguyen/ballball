package com.example.ballball.creatematch

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.*
import com.example.ballball.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class CreateMatchActivity : AppCompatActivity() {
    private lateinit var createMatchBinding: ActivityCreateMatchBinding
    private val createMatchViewModel : CreateMatchViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var layoutBottomSheetLocationBinding: LayoutBottomSheetLocationBinding
    private lateinit var createMatchSuccessDialogBinding: CreateMatchSuccessDialogBinding
    private lateinit var createMatchDialogBinding: CreateMatchDialogBinding
    private var matchDate : String? = null
    private var deviceToken : String? = null
    private var teamName : String? = null
    private var phone : String? = null
    private var teamImageUrl : String? = null
    private var locationAddress : String? = null
    private var lat : Double? = null
    private var long : Double? = null
    private var geoHash : String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createMatchBinding = ActivityCreateMatchBinding.inflate(layoutInflater)
        setContentView(createMatchBinding.root)
        handleVariables()
        initEvents()
        initObserves()
        if (userUID != null) {
            createMatchViewModel.loadTeamInfo(userUID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleVariables() {
        createMatchBinding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var currentMonth = month.plus(1).toString()
            if (currentMonth.length == 1) {
                currentMonth = "0$currentMonth"
            }
            var dayOfMonthString = dayOfMonth.toString()
            if (dayOfMonthString.length == 1) {
                dayOfMonthString = "0$dayOfMonthString"
            }
            matchDate = "$dayOfMonthString/$currentMonth/$year"
            Log.e("DATE", matchDate.toString())
        }

        MessageConnection.firebaseMessaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                } else {
                    deviceToken = task.result
                }
            })

        FirebaseDatabase.getInstance().getReference("Users").child(userUID!!).get()
            .addOnSuccessListener {
                phone = it.child("userPhone").value.toString()
            }

        FirebaseDatabase.getInstance().getReference("Teams").child(userUID).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }

        StorageConnection.storageReference.getReference("Teams").child(userUID).downloadUrl
            .addOnSuccessListener {
            teamImageUrl = it.toString()
        }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        disablePreDay()
        back()
        locationSelect()
        timeSelect()
        saveRequest()
    }

    private fun initObserves() {
        teamInfoObserve()
        saveRequestObserve()
        notificationObserve()
        saveUpComingObserve()
    }

    private fun saveUpComingObserve() {
        createMatchViewModel.saveNewCreate.observe(this) {result ->
            when (result) {
                is CreateMatchViewModel.NewCreateResult.ResultOk -> {}
                is CreateMatchViewModel.NewCreateResult.ResultError -> {}
            }
        }
    }

    private fun notificationObserve() {
        createMatchViewModel.notification.observe(this) {result ->
            when(result) {
                is CreateMatchViewModel.NotificationResult.ResultOk -> {}
                is CreateMatchViewModel.NotificationResult.ResultError -> {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveRequest() {
        createMatchBinding.saveRequest.setOnClickListener {
            if (matchDate == null) {
                val currentDate = LocalDate.now()
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
                matchDate = currentDate.format(dateFormatter)
            }
            if (createMatchBinding.pitchLocation.text.equals("Sân Đại Học Khoa Học")) {
                locationAddress = LocationAddress.khoaHocAddress
                lat = LocationAddress.khoaHocLat
                long = LocationAddress.khoaHocLong
                geoHash = LocationAddress.khoaHocHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Monaco")) {
                locationAddress = LocationAddress.monacoAddress
                lat = LocationAddress.monacoLat
                long = LocationAddress.monacoLong
                geoHash = LocationAddress.monacoHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Lâm Hoằng")) {
                locationAddress = LocationAddress.lamHoangAddress
                lat = LocationAddress.lamHoangLat
                long = LocationAddress.lamHoangLong
                geoHash = LocationAddress.lamHoangHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân An Cựu")) {
                locationAddress = LocationAddress.anCuuAddress
                lat = LocationAddress.anCuuLat
                long = LocationAddress.anCuuLong
                geoHash = LocationAddress.anCuuHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Đại Học Luật")) {
                locationAddress = LocationAddress.luatAddress
                lat = LocationAddress.luatLat
                long = LocationAddress.luatLong
                geoHash = LocationAddress.luatHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Uyên Phương")) {
                locationAddress = LocationAddress.uyenPhuongAddress
                lat = LocationAddress.uyenPhuongLat
                long = LocationAddress.uyenPhuongLong
                geoHash = LocationAddress.uyenPhuongHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Đại Học Y Dược")) {
                locationAddress = LocationAddress.yDuocAddress
                lat = LocationAddress.yDuocLat
                long = LocationAddress.yDuocLong
                geoHash = LocationAddress.yDuocHash
            }

            if (createMatchBinding.pitchLocation.text.equals("Sân Xuân Phú")) {
                locationAddress = LocationAddress.xuanPhuAddress
                lat = LocationAddress.xuanPhuLat
                long = LocationAddress.xuanPhuLong
                geoHash = LocationAddress.xuanPhuHash
            }

            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            createMatchDialogBinding = CreateMatchDialogBinding.inflate(layoutInflater)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(createMatchDialogBinding.root)
            createMatchDialogBinding.date.text = matchDate
            createMatchDialogBinding.time.text = createMatchBinding.time.text
            createMatchDialogBinding.yes.setOnClickListener {
                save()
                dialog.dismiss()
            }
            createMatchDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun save() {
        with(createMatchBinding) {
            val location = pitchLocation.text.toString()
            val time = time.text.toString()
            val note = note.text.toString()
            val teamPeopleNumber = peopleNumber.text.toString()
            val matchID = DatabaseConnection.databaseReference.getReference("Request_Match").push().key

            if (userUID != null) {
                //save request match
                createMatchViewModel.saveRequest(userUID, matchID!!,
                    deviceToken!!, teamName!!, phone!!, matchDate!!, time, location, note,
                    teamPeopleNumber, teamImageUrl!!,
                    locationAddress!!, lat!!, long!!, geoHash!!)
                //save request match notifications
                createMatchViewModel.notification(matchID, teamName!!, userUID)
                //save new create match
                createMatchViewModel.saveNewCreate(userUID, matchID,
                    deviceToken!!, teamName!!, phone!!, matchDate!!, time, location, note,
                    teamPeopleNumber, teamImageUrl!!,
                    locationAddress!!, lat!!, long!!, geoHash!!)

                Log.e("Info", userUID)
                Log.e("Info", matchID)
                Log.e("Info", deviceToken!!)
                Log.e("Info", teamName!!)
                Log.e("Info", phone!!)
                Log.e("Info", matchDate!!)
                Log.e("Info", time)
                Log.e("Info", location)
                Log.e("Info", note)
                Log.e("Info", teamPeopleNumber)
                Log.e("Info", teamImageUrl!!)
                Log.e("Info", locationAddress!!)
                Log.e("Info", lat.toString())
                Log.e("Info", long.toString())
            }
        }
    }

    private fun saveRequestObserve() {
        createMatchViewModel.saveRequest.observe(this) {result ->
            when (result) {
                is CreateMatchViewModel.SaveRequest.Loading -> {}
                is CreateMatchViewModel.SaveRequest.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    createMatchSuccessDialogBinding = CreateMatchSuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(createMatchSuccessDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    createMatchSuccessDialogBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }, 5000)
                }
                is CreateMatchViewModel.SaveRequest.ResultError -> {}
            }
        }
    }

    private fun timeSelect() {
        createMatchBinding.timeLayout.setOnClickListener {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mCurrentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, R.style.MyTimePickerTheme, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    createMatchBinding.time.text = String.format("%02d:%02d", hourOfDay, minute)
                }
            }, hour, minute, true).show()
        }
    }

    private fun locationSelect() {
        createMatchBinding.locationLayout.setOnClickListener {
            val locationDialog = BottomSheetDialog(this)
            layoutBottomSheetLocationBinding = LayoutBottomSheetLocationBinding.inflate(layoutInflater)
            locationDialog.setContentView(layoutBottomSheetLocationBinding.root)

            layoutBottomSheetLocationBinding.khoaHoc.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.khoaHoc.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.monaco.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.monaco.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.lamHoang.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.lamHoang.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.anCuu.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.anCuu.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.luat.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.luat.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.uyenPhuong.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.uyenPhuong.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.yDuoc.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.yDuoc.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.xuanPhu.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.xuanPhu.text
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            locationDialog.show()
        }
    }

    private fun teamInfoObserve() {
        createMatchViewModel.loadTeamInfo.observe(this) {result ->
            with(createMatchBinding) {
                progressBar.visibility = View.GONE
                titleLayout.visibility = View.VISIBLE
                scrollView.visibility = View.VISIBLE
            }
            when (result) {
                is CreateMatchViewModel.LoadTeamInfo.Loading -> {
                    createMatchBinding.progressBar.visibility = View.VISIBLE
                }
                is CreateMatchViewModel.LoadTeamInfo.LoadImageOk -> {
                    Glide.with(createMatchBinding.teamImage).load(result.teamImageUrl).centerCrop().into(createMatchBinding.teamImage)
                }
                is CreateMatchViewModel.LoadTeamInfo.LoadInfoOk -> {
                    createMatchBinding.pitchLocation.text = result.teamLocation
                    createMatchBinding.peopleNumber.text = result.teamPeopleNumber
                }
                is CreateMatchViewModel.LoadTeamInfo.LoadImageError -> {}
                is CreateMatchViewModel.LoadTeamInfo.LoadInfoError -> {}
            }
        }
    }

    private fun disablePreDay() {
        createMatchBinding.calendar.minDate = System.currentTimeMillis() - 1000
    }

    private fun back() {
        createMatchBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}