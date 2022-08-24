package com.example.ballball.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.ballball.R
import com.example.ballball.main.MainActivity
import com.example.ballball.onboarding.activity.OnBoardingActivity2
import com.example.ballball.user.walkthrough.name.NameActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (userUID != null) {
            DatabaseConnection.databaseReference.getReference("Teams").child(userUID).get()
                .addOnSuccessListener {
                    Handler().postDelayed({
                        if (it.exists()) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Animation.animateSlideLeft(this)
                            finish()
                        } else {
                            startActivity(Intent(this, OnBoardingActivity2::class.java))
                            Animation.animateSlideLeft(this)
                            finish()
                        }
                    }, 2000)
                }
            }

        if (userUID == null) {
            Handler().postDelayed({
                startActivity(Intent(this, OnBoardingActivity2::class.java))
                Animation.animateSlideLeft(this)
                finish()
            }, 2000)
        }
    }
}