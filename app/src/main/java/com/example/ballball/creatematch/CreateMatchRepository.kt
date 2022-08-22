package com.example.ballball.creatematch

import com.example.ballball.model.AllNotificationModel
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
        teamImageUrl : String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val requestData = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, 0, "", "",
        "", "", geoHash)
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

    fun notification(
        matchID : String,
        teamName: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val allNotification = AllNotificationModel(matchID, teamName)
        firebaseDatabase.getReference("Request_Match_Notification").child(matchID).setValue(allNotification)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                }
                else {
                    onFail(it.exception?.message.orEmpty())
                }
            }.addOnFailureListener {
                onFail(it.message.orEmpty())
            }
        }

    fun saveNewCreate(
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
        teamImageUrl : String,
        locationAddress: String,
        lat: Double,
        long: Double,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingData = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long)
        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID).setValue(upComingData)
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