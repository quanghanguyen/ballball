package com.example.ballball.utils

import com.google.firebase.auth.FirebaseAuth

object AuthConnection {
    val auth = FirebaseAuth.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val authUser = FirebaseAuth.getInstance().currentUser
}