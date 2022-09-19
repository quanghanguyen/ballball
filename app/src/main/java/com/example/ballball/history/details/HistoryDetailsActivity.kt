package com.example.ballball.history.details

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballball.databinding.ActivityHistoryDetailsBinding
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.AuthConnection
import com.example.ballball.utils.DatabaseConnection
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.clientUID
import com.example.ballball.utils.Model.teamConfirmUID
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.Model.teamPeopleNumber
import com.example.ballball.utils.Model.teamPhone

class HistoryDetailsActivity : AppCompatActivity() {

    private lateinit var historyDetailsBinding: ActivityHistoryDetailsBinding

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, HistoryDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyDetailsBinding = ActivityHistoryDetailsBinding.inflate(layoutInflater)
        setContentView(historyDetailsBinding.root)
        initEvents()
    }

    private fun initEvents() {
        binding()
        handleVariable()
        back()
        phoneCall()
        chat()
    }

    private fun handleVariable() {
        DatabaseConnection.databaseReference.getReference("Users").child(clientUID!!).get()
            .addOnSuccessListener {
                teamPhone = it.child("userPhone").value.toString()
            }
    }

    private fun chat() {
        historyDetailsBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("teamName", teamName)
            intent.putExtra("userUid", teamConfirmUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun phoneCall() {
        historyDetailsBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    1)
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$teamPhone"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun back() {
        historyDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(historyDetailsBinding) {
                teamName.text = data?.clientTeamName
                date.text = data?.date
                clientTeamName.text = data?.teamName
                homeTeamName.text = data?.clientTeamName
                Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)

                Model.teamName = data?.clientTeamName
                teamConfirmUID = data?.userUID
                clientUID = data?.clientUID
            }
        }
    }
}