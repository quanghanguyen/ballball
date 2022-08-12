package com.example.ballball.main.match.wait.details

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityWaitDetailsBinding
import com.example.ballball.databinding.SignOutDialogBinding
import com.example.ballball.databinding.SuccessDialogBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.main.match.newcreate.details.NewCreateDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchID
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamname
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitDetailsActivity : AppCompatActivity() {

    private lateinit var waitDetailsBinding : ActivityWaitDetailsBinding
    private val waitDetailsViewModel : WaitDetailsViewModel by viewModels()
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, WaitDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitDetailsBinding = ActivityWaitDetailsBinding.inflate(layoutInflater)
        setContentView(waitDetailsBinding.root)
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        binding()
        back()
        cancelWaitRequest()
        handleVariables()
    }

    private fun initObserve() {
        cancelWaitRequestObserve()
    }

    private fun handleVariables() {
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID!!).get()
            .addOnSuccessListener {
                clientTeamName = it.child("teamName").value.toString()
            }
    }

    private fun cancelWaitRequestObserve() {
        waitDetailsViewModel.cancelWaitMatch.observe(this) {result ->
            when (result) {
                is WaitDetailsViewModel.CancelWaitMatch.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Yêu cầu bắt trận của bạn đã được hủy"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is WaitDetailsViewModel.CancelWaitMatch.ResultError -> {}
                is WaitDetailsViewModel.CancelWaitMatch.NotificationOk -> {}
                is WaitDetailsViewModel.CancelWaitMatch.NotificationError -> {}
            }
        }
    }

    private fun cancelWaitRequest() {
        waitDetailsBinding.cancelRequest.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Hủy yêu cầu"
            signOutDialogBinding.content.text = "Bạn có muốn hủy yêu cầu bắt trận với $teamname không?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    waitDetailsViewModel.cancelWaitMatch(userUID, matchID!!, matchDate!!, matchTime!!, clientTeamName!!)
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
        waitDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(waitDetailsBinding) {
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
                note.text = data?.note
                matchID = data?.matchID
                teamname = data?.teamName
                matchDate = data?.date
                matchTime = data?.time

//                Model.matchID = data?.matchID
//                destinationLat = data?.lat
//                destinationLong = data?.long
//                destinationAddress = data?.locationAddress
            }
        }
    }
}