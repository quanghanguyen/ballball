package com.example.ballball.user.userinfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.ballball.databinding.ActivityUserInformationBinding
import com.example.ballball.user.teaminformation.TeamInformationActivity
import com.example.ballball.utils.Animation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UserInformationActivity : AppCompatActivity() {

    private lateinit var userInformationBinding: ActivityUserInformationBinding
    private val userInformationViewModel : UserInformationViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val localFile = File.createTempFile("tempImage", "jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInformationBinding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(userInformationBinding.root)
        initEvents()
        initObserve()
        if (userUID != null) {
            userInformationViewModel.loadAvatar(userUID, localFile)
        }
        if (userUID != null) {
            userInformationViewModel.loadNameAndPhone(userUID)
        }
    }

    private fun initEvents() {
        back()
        teamInformation()
    }

    private fun teamInformation() {
        userInformationBinding.teamInformation.setOnClickListener {
            startActivity(Intent(this, TeamInformationActivity::class.java))
            Animation.animateSlideLeft(this)
        }
    }

    private fun back() {
        userInformationBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun initObserve() {
        loadAvatarObserver()
        loadNameAndPhoneObserve()
    }

    private fun loadAvatarObserver() {
        userInformationViewModel.loadAvatar.observe(this) {result->
            when (result) {
                is UserInformationViewModel.LoadAvatar.ResultOk -> {
                    userInformationBinding.profilePicture.setImageBitmap(result.image)
                }
                is UserInformationViewModel.LoadAvatar.ResultError -> {}
            }
        }
    }

    private fun loadNameAndPhoneObserve() {
        userInformationViewModel.loadNameAndPhone.observe(this) {result ->
            when (result) {
                is UserInformationViewModel.LoadNameAndPhone.ResultOk -> {
                    userInformationBinding.userName.text = result.userName
                    userInformationBinding.userPhoneNumber.text = result.userPhone
                }
                is UserInformationViewModel.LoadNameAndPhone.ResultError -> {}
            }
        }
    }
}