package com.example.ballball.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.ballball.R
import com.example.ballball.main.MainActivity
import com.example.ballball.onboarding.activity.OnBoardingActivity2
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private var teamName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (userUID != null) {
            DatabaseConnection.databaseReference.getReference("Teams").child(userUID).get()
                .addOnSuccessListener {
                    teamName = it.child("teamName").value.toString()
                    Log.e("TEAMNAME", teamName!!)
                }
            }

        Handler().postDelayed({
            if (teamName == null) {
                startActivity(Intent(this, OnBoardingActivity2::class.java))
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 2000)
    }
}