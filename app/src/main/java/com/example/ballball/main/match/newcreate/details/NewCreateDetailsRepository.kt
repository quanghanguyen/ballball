package com.example.ballball.main.match.newcreate.details

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class NewCreateDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun deleteNewCreate(
        userUID : String, matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").child(matchID).removeValue()


        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
        }
    }