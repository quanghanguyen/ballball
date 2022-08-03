package com.example.ballball.user.userinfomation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.ballball.R
import com.example.ballball.databinding.ActivityUserInformationBinding
import com.example.ballball.databinding.LayoutBottomSheetDialogBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.login.phone.login.SignInActivity
import com.example.ballball.onboarding.activity.OnBoardingActivity2
import com.example.ballball.user.teaminformation.TeamInformationActivity
import com.example.ballball.utils.Animation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UserInformationActivity : AppCompatActivity() {

    private lateinit var userInformationBinding: ActivityUserInformationBinding
    private val userInformationViewModel : UserInformationViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val localFile = File.createTempFile("tempImage", "jpg")
    private lateinit var layoutBottomSheetDialogBinding: LayoutBottomSheetDialogBinding
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var imgUri : Uri

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
        editAvatar()
        signOut()
    }

    private fun signOut() {
        userInformationBinding.signOut.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        val dialog = Dialog(this, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(signOutDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        signOutDialogBinding.yes.setOnClickListener {
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finishAffinity()
            Animation.animateSlideLeft(this)
        }
        signOutDialogBinding.no.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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

    private fun editAvatar() {
        userInformationBinding.editProfilePictureButton.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        layoutBottomSheetDialogBinding = LayoutBottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(layoutBottomSheetDialogBinding.root)

        layoutBottomSheetDialogBinding.takePhoto.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                selectAvatarFromCamera()
                dialog.dismiss()
                Animation.animateFade(this)
            }
        }

        layoutBottomSheetDialogBinding.gallery.setOnClickListener{
            selectAvatar()
            dialog.dismiss()
            Animation.animateFade(this)
        }
        dialog.show()
    }

    private fun selectAvatar() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 0)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectAvatarFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "Camera không hoạt động", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK) {
            imgUri = data?.data!!
            userInformationBinding.profilePicture.setImageURI(imgUri)
        }

        if (requestCode == 1 && resultCode == RESULT_OK)  {
            val bundle : Bundle? = data?.extras
            val finalPhoto : Bitmap = bundle?.get("data") as Bitmap
            userInformationBinding.profilePicture.setImageBitmap(finalPhoto)
        }

        if (this::imgUri.isInitialized) {
            if (userUID != null) {
                userInformationViewModel.saveAvatar(imgUri, userUID)
            }
        }
    }

    private fun initObserve() {
        loadAvatarObserver()
        loadNameAndPhoneObserve()
        saveAvatarObserve()
    }

    private fun loadAvatarObserver() {
        userInformationViewModel.loadAvatar.observe(this) {result ->
            with(userInformationBinding) {
                progressBar.visibility = View.GONE
                titleLayout.visibility = View.VISIBLE
                profilePicture.visibility = View.VISIBLE
                editProfilePictureButton.visibility = View.VISIBLE
                userName.visibility = View.VISIBLE
                userPhoneNumber.visibility = View.VISIBLE
                line.visibility = View.VISIBLE
                contentLayout.visibility = View.VISIBLE
                signOut.visibility = View.VISIBLE
            }

            when (result) {
                is UserInformationViewModel.LoadAvatar.Loading -> {
                    userInformationBinding.progressBar.visibility = View.VISIBLE
                }
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

    private fun saveAvatarObserve() {
        userInformationViewModel.saveAvatar.observe(this) {result ->
            when (result) {
                is UserInformationViewModel.SaveAvatar.ResultOk -> {}
                is UserInformationViewModel.SaveAvatar.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}