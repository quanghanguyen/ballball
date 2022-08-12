package com.example.ballball.main.match.upcoming.details

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.databinding.ActivityConfirmDetailsBinding
import com.example.ballball.databinding.ActivityUpComingDetailsBinding
import com.example.ballball.main.match.confirm.details.ConfirmDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model

class UpComingDetailsActivity : AppCompatActivity() {

    private lateinit var upComingDetailsBinding: ActivityUpComingDetailsBinding

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
    }

    private fun initEvents() {
        binding()
        back()
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
                teamName.text = data?.clientTeamName
                Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                note.text = data?.note


                Model.matchID = data?.matchID
                Model.deviceToken = data?.deviceToken
                Model.teamName = data?.teamName
                Model.teamPhone = data?.teamPhone
                Model.matchDate = data?.date
                Model.matchTime = data?.time
                Model.matchLocation = data?.location
                Model.teamNote = data?.note
                Model.teamPeopleNumber = data?.teamPeopleNumber
                Model.teamImageUrl = data?.teamImageUrl
                Model.locationAddress = data?.locationAddress
                Model.lat = data?.lat
                Model.long = data?.long
                Model.click = data?.click
                Model.clientTeamName = data?.clientTeamName
                Model.clientImageUrl = data?.clientImageUrl
                }
            }
        }

    }