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
import com.example.ballball.utils.Animation
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private var teamName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (userUID != null){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Animation.animateSlideLeft(this)
            } else {
                startActivity(Intent(this, OnBoardingActivity2::class.java))
                finish()
                Animation.animateSlideLeft(this)
            }
        }, 2000)

//        if (userUID != null) {
//
////            DatabaseConnection.databaseReference.getReference("Teams").child(userUID).get()
////                .addOnSuccessListener {
////                    teamName = it.child("teamName").value.toString()
////                    Log.e("TEAMNAME", teamName!!)
////                }
//
//            Handler().postDelayed({
//                    startActivity(Intent(this, OnBoardingActivity2::class.java))
//                    finish()
//
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//            }, 2000)
//        }
//
////        Handler().postDelayed({
////            if (teamName == null) {
////                startActivity(Intent(this, OnBoardingActivity2::class.java))
////                finish()
////            } else {
////                startActivity(Intent(this, MainActivity::class.java))
////                finish()
////            }
////        }, 2000)
    }
}