package com.example.ballball.user.walkthrough.avatar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ballball.R
import com.example.ballball.databinding.ActivityAvatarBinding
import com.example.ballball.databinding.LayoutBottomSheetDialogBinding
import com.example.ballball.user.walkthrough.team.TeamActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.StorageConnection
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvatarActivity : AppCompatActivity() {

    private lateinit var avatarBinding : ActivityAvatarBinding
    private lateinit var layoutBottomSheetDialogBinding: LayoutBottomSheetDialogBinding
    private val avatarViewModel : AvatarViewModel by viewModels()
    private lateinit var imgUri : Uri
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private var userAvatarUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avatarBinding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(avatarBinding.root)
        initEvents()
        initAvatarObserve()
    }

    private fun initEvents() {
        back()
        next()
        addAvatar()
    }

    private fun initAvatarObserve() {
        avatarViewModel.saveAvatar.observe(this) {result ->
            when (result) {
                is AvatarViewModel.SaveAvatar.ResultOk -> {}
                is AvatarViewModel.SaveAvatar.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addAvatar() {
        avatarBinding.editProfilePictureButton.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectAvatarFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "Camera không hoạt động", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectAvatar() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK) {
            imgUri = data?.data!!
            avatarBinding.profilePicture.setImageURI(imgUri)
        }

        if (requestCode == 1 && resultCode == RESULT_OK)  {
            val bundle : Bundle? = data?.extras
            val finalPhoto : Bitmap = bundle?.get("data") as Bitmap
            avatarBinding.profilePicture.setImageBitmap(finalPhoto)
        }
    }

    private fun next() {
        avatarBinding.next.setOnClickListener {
            if (this::imgUri.isInitialized) {
                if (userUID != null) {
                    avatarViewModel.saveAvatar(imgUri, userUID)
                }
                startActivity(Intent(this, TeamActivity::class.java))
                Animation.animateSlideLeft(this)
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh đại diện", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun back() {
        avatarBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
}