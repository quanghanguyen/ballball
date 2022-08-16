package com.example.ballball.main.match.upcoming.details

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.*
import com.example.ballball.main.match.confirm.details.ConfirmDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.clientTeamName
import com.example.ballball.utils.Model.clientUID
import com.example.ballball.utils.Model.matchDate
import com.example.ballball.utils.Model.matchID
import com.example.ballball.utils.Model.matchTime
import com.example.ballball.utils.Model.teamName
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpComingDetailsActivity : AppCompatActivity() {

    private lateinit var upComingDetailsBinding: ActivityUpComingDetailsBinding
    private val upComingDetailsViewModel : UpComingDetailsViewModel by viewModels()
    private lateinit var cancelMatchDialogBinding: CancelMatchDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var successDialogBinding: SuccessDialogBinding

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, UpComingDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upComingDetailsBinding = ActivityUpComingDetailsBinding.inflate(layoutInflater)
        setContentView(upComingDetailsBinding.root)
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        binding()
        back()
        cancelMatch()
    }

    private fun initObserve() {
        cancelUpComingMatchObserve()
    }

    private fun cancelMatch() {
        upComingDetailsBinding.cancelMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            cancelMatchDialogBinding = CancelMatchDialogBinding.inflate(layoutInflater)
            dialog.setContentView(cancelMatchDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            cancelMatchDialogBinding.yes.setOnClickListener {
                val id : Int = cancelMatchDialogBinding.radioGroup.checkedRadioButtonId
                Log.e("ID", id.toString())
                if (id != -1) {
                    val radio : RadioButton = cancelMatchDialogBinding.root.findViewById(id)
                    val radioText = radio.text.toString()
                    if (userUID != null) {
                        upComingDetailsViewModel.cancelUpComingMatch(clientUID!!, userUID, matchID!!, matchDate!!, matchTime!!,
                        clientTeamName!!, radioText)
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Vui lòng chọn lí do", Toast.LENGTH_SHORT).show()
                }
            }
            cancelMatchDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun cancelUpComingMatchObserve() {
        upComingDetailsViewModel.cancelUpComing.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.CancelUpComing.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Trận đấu này đã được hủy"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is UpComingDetailsViewModel.CancelUpComing.ResultError -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationOk -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationError -> {}
            }
        }
    }

    private fun back() {
        upComingDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(upComingDetailsBinding) {
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                note.text = data?.note

                if (userUID == data?.userUID) {
                    Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)
                    teamName.text = data?.clientTeamName
                } else {
                    Glide.with(teamImage).load(data?.teamImageUrl).centerCrop().into(teamImage)
                    teamName.text = data?.teamName
                }

                matchID = data?.matchID
                Model.deviceToken = data?.deviceToken
                Model.teamName = data?.teamName
                Model.teamPhone = data?.teamPhone
                matchDate = data?.date
                matchTime = data?.time
                Model.matchLocation = data?.location
                Model.teamNote = data?.note
                Model.teamPeopleNumber = data?.teamPeopleNumber
                Model.teamImageUrl = data?.teamImageUrl
                Model.locationAddress = data?.locationAddress
                Model.lat = data?.lat
                Model.long = data?.long
                Model.click = data?.click
                clientTeamName = data?.clientTeamName
                Model.clientImageUrl = data?.clientImageUrl
                clientUID = data?.clientUID
                }
            }
        }
    }