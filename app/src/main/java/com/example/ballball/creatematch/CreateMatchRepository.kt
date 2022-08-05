package com.example.ballball.creatematch

import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class CreateMatchRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun sendRequest(
        userUID : String,
        matchID : String,
        deviceToken : String,
        teamName : String,
        teamPhone : String,
        date : String,
        time : String,
        location : String,
        note : String,
        teamPeopleNumber : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val requestData = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date, time, location, note, teamPeopleNumber)
        firebaseDatabase.getReference("Request_Match").child(matchID).setValue(requestData)
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