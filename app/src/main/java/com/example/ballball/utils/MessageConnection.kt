package com.example.ballball.utils

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessaging

object MessageConnection {
    @SuppressLint("StaticFieldLeak")
    val firebaseMessaging = FirebaseMessaging.getInstance()
}