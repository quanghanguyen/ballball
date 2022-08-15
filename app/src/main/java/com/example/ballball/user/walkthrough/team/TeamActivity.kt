package com.example.ballball.user.walkthrough.team

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ballball.R
import com.example.ballball.databinding.ActivityTeamBinding
import com.example.ballball.databinding.LayoutBottomSheetLocationBinding
import com.example.ballball.databinding.LayoutBottomSheetPeopleNumberBinding
import com.example.ballball.main.MainActivity
import com.example.ballball.utils.Animation
import com.example.ballball.loadingdialog.LoadingDialog
import com.example.ballball.utils.MessageConnection
import com.example.ballball.utils.Model.deviceToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamActivity : AppCompatActivity() {

    private lateinit var teamBinding: ActivityTeamBinding
    private lateinit var layoutBottomSheetLocationBinding: LayoutBottomSheetLocationBinding
    private lateinit var layoutBottomSheetPeopleNumberBinding: LayoutBottomSheetPeopleNumberBinding
    private val teamViewModel : TeamViewModel by viewModels()
    private lateinit var imgUri : Uri
    private lateinit var loadingDialog: LoadingDialog
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamBinding = ActivityTeamBinding.inflate(layoutInflater)
        setContentView(teamBinding.root)
        loadingDialog = LoadingDialog()
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        handleVariables()
        locationSelect()
        peopleNumberSelect()
        selectTeamImage()
        back()
        next()
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

    private fun next() {
        teamBinding.next.setOnClickListener {
            if (
                this::imgUri.isInitialized
                || teamBinding.teamName.text.isNotEmpty()
                || teamBinding.location.text.isNotEmpty()
                || teamBinding.peopleNumber.text.isNotEmpty()
            ) {
                if (userUid != null) {
                    teamViewModel.saveTeamsImage(imgUri, userUid)
                    teamViewModel.saveTeams(userUid, teamBinding.teamName.text.toString(),
                        teamBinding.location.text.toString(), teamBinding.peopleNumber.text.toString(),
                        teamBinding.note.text.toString(), deviceToken!!)
                }
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun back() {
        teamBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun selectTeamImage() {
        teamBinding.addTeamImage.setOnClickListener {
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
            teamBinding.teamImage.setImageURI(imgUri)
        }
    }

    private fun initObserve() {
        saveTeamObserve()
        saveTeamImageObserve()
    }

    private fun saveTeamObserve() {
        teamViewModel.saveTeams.observe(this) {result ->
            when (result) {
                is TeamViewModel.SaveTeams.ResultOk -> {}
                is TeamViewModel.SaveTeams.ResultError -> {}
            }
        }
    }

    private fun saveTeamImageObserve() {
        teamViewModel.saveTeamsImage.observe(this) {result ->
            loadingDialog.dismissProgressDialog()
            when (result) {
                is TeamViewModel.SaveTeamsImage.Loading -> {
                    loadingDialog.loadingProgressDialog(this)
                }
                is TeamViewModel.SaveTeamsImage.ResultOk -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Animation.animateSlideLeft(this)
                }
                is TeamViewModel.SaveTeamsImage.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun peopleNumberSelect() {
        teamBinding.peopleNumber.setOnClickListener {
            showPeopleNumberBottomSheetDialog()
        }
    }

    private fun locationSelect() {
        teamBinding.location.setOnClickListener {
            showLocationBottomSheetDialog()
        }
    }

    private fun showLocationBottomSheetDialog() {
        val locationDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        layoutBottomSheetLocationBinding = LayoutBottomSheetLocationBinding.inflate(layoutInflater)
        locationDialog.setContentView(layoutBottomSheetLocationBinding.root)

        layoutBottomSheetLocationBinding.khoaHoc.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.khoaHoc.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.monaco.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.monaco.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.lamHoang.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.lamHoang.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.anCuu.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.anCuu.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.luat.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.luat.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.uyenPhuong.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.uyenPhuong.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.yDuoc.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.yDuoc.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        layoutBottomSheetLocationBinding.xuanPhu.setOnClickListener {
            teamBinding.location.text = layoutBottomSheetLocationBinding.xuanPhu.text
            locationDialog.dismiss()
            Animation.animateFade(this)
        }
        locationDialog.show()
    }

    private fun showPeopleNumberBottomSheetDialog() {
        val peopleNumberDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        layoutBottomSheetPeopleNumberBinding = LayoutBottomSheetPeopleNumberBinding.inflate(layoutInflater)
        peopleNumberDialog.setContentView(layoutBottomSheetPeopleNumberBinding.root)

        layoutBottomSheetPeopleNumberBinding.five.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.five.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.six.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.six.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.seven.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.seven.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.eight.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.eight.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.nine.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.nine.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.ten.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.ten.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }

        layoutBottomSheetPeopleNumberBinding.eleven.setOnClickListener {
            teamBinding.peopleNumber.text = layoutBottomSheetPeopleNumberBinding.eleven.text
            peopleNumberDialog.dismiss()
            Animation.animateFade(this)
        }
        peopleNumberDialog.show()
    }
}