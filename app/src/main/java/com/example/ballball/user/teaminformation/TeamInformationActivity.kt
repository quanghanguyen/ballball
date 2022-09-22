package com.example.ballball.user.teaminformation

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.*
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
    private lateinit var layoutBottomSheetLocationBinding: LayoutBottomSheetLocationBinding
    private lateinit var layoutBottomSheetPeopleNumberBinding: LayoutBottomSheetPeopleNumberBinding
    private lateinit var imgUri : Uri
    private lateinit var successDialogBinding: SuccessDialogBinding
    private lateinit var loadingDialogBinding: LoadingDialogBinding
    private lateinit var loadingDialog : Dialog

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
                is TeamInformationViewModel.LoadTeamInfo.LoadInfoError -> {}
            }
        }
    }

    private fun saveTeamObserve() {
        teamInformationViewModel.saveTeams.observe(this) {result ->
            when (result) {
                is TeamInformationViewModel.SaveTeams.ResultOk -> {}
                is TeamInformationViewModel.SaveTeams.ResultError -> {}
            }
        }
    }

    private fun saveTeamImageObserve() {
        teamInformationViewModel.saveTeamsImage.observe(this) {result ->
            when (result) {
                is TeamInformationViewModel.SaveTeamsImage.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Lưu thông tin thành công"
                    successDialogBinding.next.setOnClickListener {
                        dialog.cancel()
                    }
                    loadingDialog.dismiss()
                    dialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        dialog.cancel()
                    }, 5000)
                }
                is TeamInformationViewModel.SaveTeamsImage.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun save() {
        loadingDialog = Dialog(this, R.style.MyAlertDialogTheme)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setContentView(loadingDialogBinding.root)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        teamInformationBinding.save.setOnClickListener {
            if (teamInformationBinding.teamName.text.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Tên đội", Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.show()
                if (this::imgUri.isInitialized) {
                    if (userUID != null) {
                        teamInformationViewModel.saveTeamsImage(imgUri, userUID)
                    }
                }
                if (userUID != null) {
                    teamInformationViewModel.saveTeams(userUID, teamInformationBinding.teamName.text.toString(),
                        teamInformationBinding.location.text.toString(), teamInformationBinding.peopleNumber.text.toString(),
                        teamInformationBinding.note.text.toString(), deviceToken!!)
                }
            }
        }
    }

    private fun selectTeamImage() {
        teamInformationBinding.teamImageLayout.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, 0)
                Animation.animateSlideLeft(this)
            }
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

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}