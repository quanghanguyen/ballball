package com.example.ballball.user.teaminformation

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityTeamInformationBinding
import com.example.ballball.databinding.LayoutBottomSheetLocationBinding
import com.example.ballball.databinding.LayoutBottomSheetPeopleNumberBinding
import com.example.ballball.main.MainActivity
import com.example.ballball.user.userinfomation.UserInformationViewModel
import com.example.ballball.user.walkthrough.team.TeamViewModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.MessageConnection
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.deviceToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class TeamInformationActivity : AppCompatActivity() {

    private lateinit var teamInformationBinding: ActivityTeamInformationBinding
    private val teamInformationViewModel: TeamInformationViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
//    private val localFile = File.createTempFile("tempImage", "jpg")
    private lateinit var layoutBottomSheetLocationBinding: LayoutBottomSheetLocationBinding
    private lateinit var layoutBottomSheetPeopleNumberBinding: LayoutBottomSheetPeopleNumberBinding
    private lateinit var imgUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamInformationBinding = ActivityTeamInformationBinding.inflate(layoutInflater)
        setContentView(teamInformationBinding.root)
        initEvents()
        initObserve()
        if (userUID != null) {
            teamInformationViewModel.loadTeamInfo(userUID)
        }
    }

    private fun initEvents() {
        handleVariables()
        back()
        locationSelect()
        peopleNumberSelect()
        selectTeamImage()
        save()
    }

    private fun handleVariables() {
        MessageConnection.firebaseMessaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                } else {
                    deviceToken = task.result
                }
            })
    }

    private fun initObserve() {
        teamInfoObserve()
        saveTeamImageObserve()
        saveTeamObserve()
    }

    private fun teamInfoObserve() {
        teamInformationViewModel.loadTeamInfo.observe(this) {result ->
            with(teamInformationBinding) {
                progressBar.visibility = View.GONE
                titleLayout.visibility = View.VISIBLE
                scrollView.visibility = View.VISIBLE
            }
            when (result) {
                is TeamInformationViewModel.LoadTeamInfo.Loading -> {
                    teamInformationBinding.progressBar.visibility = View.VISIBLE
                }
//                is TeamInformationViewModel.LoadTeamInfo.LoadImageOk -> {
//                    teamInformationBinding.teamImage.setImageBitmap(result.image)
//                }
                is TeamInformationViewModel.LoadTeamInfo.LoadInfoOk -> {
                    teamInformationBinding.teamName.setText(result.teamName)
                    teamInformationBinding.location.text = result.teamLocation
                    teamInformationBinding.peopleNumber.text = result.teamPeopleNumber
                    teamInformationBinding.note.setText(result.teamNote)
                    Glide.with(teamInformationBinding.teamImage)
                        .load(result.teamImageUrl)
                        .centerCrop()
                        .into(teamInformationBinding.teamImage)
                }
//                is TeamInformationViewModel.LoadTeamInfo.LoadImageError -> {}
                is TeamInformationViewModel.LoadTeamInfo.LoadInfoError -> {}
            }
        }
    }

    private fun saveTeamObserve() {
        teamInformationViewModel.saveTeams.observe(this) {result ->
            when (result) {
                is TeamInformationViewModel.SaveTeams.ResultOk -> {
                    finish()
                    Animation.animateSlideRight(this)
                }
                is TeamInformationViewModel.SaveTeams.ResultError -> {}
            }
        }
    }

    private fun saveTeamImageObserve() {
        teamInformationViewModel.saveTeamsImage.observe(this) {result ->
            when (result) {
                is TeamInformationViewModel.SaveTeamsImage.ResultOk -> {
                    finish()
                    Animation.animateSlideLeft(this)
                }
                is TeamInformationViewModel.SaveTeamsImage.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun save() {
        teamInformationBinding.save.setOnClickListener {
            if (this::imgUri.isInitialized) {
                if (userUID != null) {
                    teamInformationViewModel.saveTeamsImage(imgUri, userUID)
                }
            }
            if (teamInformationBinding.teamName.text.isNotEmpty()) {
                if (userUID != null) {
                    teamInformationViewModel.saveTeams(userUID, teamInformationBinding.teamName.text.toString(),
                        teamInformationBinding.location.text.toString(), teamInformationBinding.peopleNumber.text.toString(),
                        teamInformationBinding.note.text.toString(), deviceToken!!)
                } else {
                    Toast.makeText(this, "Vui lòng nhập Tên đội", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectTeamImage() {
        teamInformationBinding.addTeamImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK) {
            imgUri = data?.data!!
            teamInformationBinding.teamImage.setImageURI(imgUri)
        }
    }

    private fun locationSelect() {
        teamInformationBinding.location.setOnClickListener {
            showLocationBottomSheetDialog()
        }
    }

    private fun showLocationBottomSheetDialog() {
        val locationDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        layoutBottomSheetLocationBinding = LayoutBottomSheetLocationBinding.inflate(layoutInflater)
        locationDialog.setContentView(layoutBottomSheetLocationBinding.root)

        layoutBottomSheetLocationBinding.khoaHoc.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.khoaHoc.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.monaco.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.monaco.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.lamHoang.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.lamHoang.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.anCuu.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.anCuu.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.luat.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.luat.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.uyenPhuong.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.uyenPhuong.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.yDuoc.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.yDuoc.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.xuanPhu.setOnClickListener {
            teamInformationBinding.location.text = layoutBottomSheetLocationBinding.xuanPhu.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        locationDialog.show()
    }

    private fun peopleNumberSelect() {
        teamInformationBinding.peopleNumber.setOnClickListener {
            showPeopleNumberBottomSheetDialog()
        }
    }

    private fun showPeopleNumberBottomSheetDialog() {
        val peopleNumberDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        layoutBottomSheetPeopleNumberBinding = LayoutBottomSheetPeopleNumberBinding.inflate(layoutInflater)
        peopleNumberDialog.setContentView(layoutBottomSheetPeopleNumberBinding.root)

        layoutBottomSheetPeopleNumberBinding.five.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.five.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.six.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.six.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.seven.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.seven.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.eight.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.eight.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.nine.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.nine.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.ten.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.ten.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.eleven.setOnClickListener {
            teamInformationBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.eleven.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }
        peopleNumberDialog.show()
    }

    private fun back() {
        teamInformationBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
}