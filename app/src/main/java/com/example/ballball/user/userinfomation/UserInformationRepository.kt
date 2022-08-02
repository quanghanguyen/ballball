package com.example.ballball.user.userinfomation

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class UserInformationRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadNameAndPhone(
        userUID : String,
        onSuccess : (DataSnapshot) -> Unit,
        onFail : (Exception) -> Unit
        ) {
        firebaseDatabase.getReference("Users").child(userUID).get()
            .addOnSuccessListener {
                onSuccess(it)
            }
            .addOnFailureListener {
                onFail(it)
            }
        }
    }