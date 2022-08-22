package com.example.ballball.creatematch

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
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
import java.io.File
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

    private fun handleVariables() {
        createMatchBinding.calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val currentMonth = month + 1
            matchDate = "$dayOfMonth/$currentMonth/$year"
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

    private fun saveRequest() {
        createMatchBinding.saveRequest.setOnClickListener {
            if (matchDate == null) {
                Toast.makeText(applicationContext, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show()
            }
            if (locationAddress == null) {
                Toast.makeText(applicationContext, "Vui lòng chọn sân", Toast.LENGTH_SHORT).show()
            } else {
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
                createMatchViewModel.notification(matchID, teamName!!)
                //save new create match
                createMatchViewModel.saveNewCreate(userUID, matchID,
                    deviceToken!!, teamName!!, phone!!, matchDate!!, time, location, note,
                    teamPeopleNumber, teamImageUrl!!,
                    locationAddress!!, lat!!, long!!)

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
                        Animation.animateInAndOut(this)
                    }
                    dialog.show()
                }
                is CreateMatchViewModel.SaveRequest.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun timeSelect() {
        createMatchBinding.timeLayout.setOnClickListener {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mCurrentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    createMatchBinding.time.text = String.format("%d:%d", hourOfDay, minute)
//                    if (hourOfDay >= mCurrentTime.get(Calendar.HOUR_OF_DAY)
//                        && minute > mCurrentTime.get(Calendar.MINUTE)
//                    ) {
//                        createMatchBinding.time.text = String.format("%d:%d", hourOfDay, minute)
//                    } else {
//                        Toast.makeText(applicationContext, "Vui lòng chọn thời gian hợp lệ", Toast.LENGTH_SHORT).show()
//                    }
                }
            }, hour, minute, true).show()
        }
    }

    private fun locationSelect() {
        createMatchBinding.locationLayout.setOnClickListener {
            val locationDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
            layoutBottomSheetLocationBinding = LayoutBottomSheetLocationBinding.inflate(layoutInflater)
            locationDialog.setContentView(layoutBottomSheetLocationBinding.root)

            layoutBottomSheetLocationBinding.khoaHoc.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.khoaHoc.text
                locationAddress = LocationAddress.khoaHocAddress
                lat = LocationAddress.khoaHocLat
                long = LocationAddress.khoaHocLong
                geoHash = LocationAddress.khoaHocHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.monaco.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.monaco.text
                locationAddress = LocationAddress.monacoAddress
                lat = LocationAddress.monacoLat
                long = LocationAddress.monacoLong
                geoHash = LocationAddress.monacoHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.lamHoang.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.lamHoang.text
                locationAddress = LocationAddress.lamHoangAddress
                lat = LocationAddress.lamHoangLat
                long = LocationAddress.lamHoangLong
                geoHash = LocationAddress.lamHoangHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.anCuu.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.anCuu.text
                locationAddress = LocationAddress.anCuuAddress
                lat = LocationAddress.anCuuLat
                long = LocationAddress.anCuuLong
                geoHash = LocationAddress.anCuuHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.luat.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.luat.text
                locationAddress = LocationAddress.luatAddress
                lat = LocationAddress.luatLat
                long = LocationAddress.luatLong
                geoHash = LocationAddress.luatHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.uyenPhuong.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.uyenPhuong.text
                locationAddress = LocationAddress.uyenPhuongAddress
                lat = LocationAddress.uyenPhuongLat
                long = LocationAddress.uyenPhuongLong
                geoHash = LocationAddress.uyenPhuongHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.yDuoc.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.yDuoc.text
                locationAddress = LocationAddress.yDuocAddress
                lat = LocationAddress.yDuocLat
                long = LocationAddress.yDuocLong
                geoHash = LocationAddress.yDuocHash
                locationDialog.dismiss()
                Animation.animateFade(this)
            }
            layoutBottomSheetLocationBinding.xuanPhu.setOnClickListener {
                createMatchBinding.pitchLocation.text = layoutBottomSheetLocationBinding.xuanPhu.text
                locationAddress = LocationAddress.xuanPhuAddress
                lat = LocationAddress.xuanPhuLat
                long = LocationAddress.xuanPhuLong
                geoHash = LocationAddress.xuanPhuHash
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
            Animation.animateInAndOut(this)
        }
    }
}