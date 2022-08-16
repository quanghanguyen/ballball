package com.example.ballball.main.match.confirm.details

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityConfirmDetailsBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.databinding.SuccessDialogBinding
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.click
import com.example.ballball.utils.Model.clientImageUrl
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.confirmUID
import com.example.ballball.utils.Model.deviceToken
import com.example.ballball.utils.Model.lat
import com.example.ballball.utils.Model.locationAddress
import com.example.ballball.utils.Model.long
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchID
import com.example.ballball.utils.Model.matchLocation
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamImageUrl
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.Model.teamNote
import com.example.ballball.utils.Model.teamPeopleNumber
import com.example.ballball.utils.Model.teamPhone
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmDetailsActivity : AppCompatActivity() {

    private lateinit var confirmDetailsBinding : ActivityConfirmDetailsBinding
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val confirmDetailsViewModel : ConfirmDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, ConfirmDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        confirmDetailsBinding = ActivityConfirmDetailsBinding.inflate(layoutInflater)
        setContentView(confirmDetailsBinding.root)
        initEvents()
        initObserves()
    }

    private fun initEvents() {
        binding()
        back()
        denyConfirmMatch()
        acceptMatch()
    }

    private fun initObserves() {
        denyConfirmMatchObserve()
        acceptMatchObserve()
        saveUpComingClientObserve()
    }

    private fun saveUpComingClientObserve() {
        confirmDetailsViewModel.saveUpComingClient.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientOk -> {}
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientError -> {}
            }
        }
    }

    private fun acceptMatchObserve() {
        confirmDetailsViewModel.acceptMatch.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Thành Công"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationError -> {}
            }
        }
    }

    private fun acceptMatch() {
        confirmDetailsBinding.acceptRequest.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Đồng ý"
            signOutDialogBinding.content.text = "Bạn đồng ý yêu cầu bắt trận của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.acceptMatch(userUID, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!,
                        matchTime!!, matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!,
                        lat!!, long!!, click!!, clientTeamName!!, clientImageUrl!!, confirmUID!!, confirmUID!!)
                }
                if (userUID != null) {
                    confirmDetailsViewModel.saveUpComingClient(userUID, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!,
                        matchTime!!, matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!,
                        lat!!, long!!, click!!, clientTeamName!!, clientImageUrl!!, confirmUID!!, userUID)
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun denyConfirmMatchObserve() {
        confirmDetailsViewModel.denyConfirmMatch.observe(this) {result ->
            when (result) {
                is ConfirmDetailsViewModel.DenyConfirmMatch.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Bạn đã từ chối yêu cầu của $clientTeamName"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is ConfirmDetailsViewModel.DenyConfirmMatch.ResultError -> {}
            }
        }
    }

    private fun denyConfirmMatch() {
        confirmDetailsBinding.denyMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Từ chối"
            signOutDialogBinding.content.text = "Bạn muốn từ chối yêu cầu bắt trận của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.denyConfirmMatch(userUID, matchID!!, confirmUID!!)
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
        confirmDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(confirmDetailsBinding) {
                teamName.text = data?.clientTeamName
                Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                note.text = data?.note


                matchID = data?.matchID
                deviceToken = data?.deviceToken
                Model.teamName = data?.teamName
                teamPhone = data?.teamPhone
                matchDate = data?.date
                matchTime = data?.time
                matchLocation = data?.location
                teamNote = data?.note
                teamPeopleNumber = data?.teamPeopleNumber
                teamImageUrl = data?.teamImageUrl
                Model.locationAddress = data?.locationAddress
                lat = data?.lat
                long = data?.long
                click = data?.click
                clientTeamName = data?.clientTeamName
                clientImageUrl = data?.clientImageUrl
                confirmUID = data?.confirmUID
            }
        }
    }
}