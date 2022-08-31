package com.example.ballball.main.match.upcoming.details

import com.example.ballball.model.CancelUpComingModel
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class UpComingDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun cancelMatch(
        userUID : String,
        clientUID : String,
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("upComingMatch").child(userUID).child(matchID).removeValue()
        firebaseDatabase.getReference("upComingMatch").child(clientUID).child(matchID).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.toString())
            }
        }

    fun restoreMatch (
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

        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID).setValue(requestData)
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

    fun cancelMatchNotification (
        clientUID: String,
        userUID: String,
        matchID: String,
        date: String,
        time: String,
        teamName: String,
        reason : String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val acceptMatchNotification = CancelUpComingModel(clientUID, userUID, matchID, date, time, teamName, reason)
        firebaseDatabase.getReference("cancelUpComing_Notification").child(userUID).child(matchID).setValue(acceptMatchNotification)
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

    fun cancelUpComingListNotification(
        clientUID : String,
        clientTeamName: String,
        clientImageUrl: String,
        id : String,
        date : String,
        time: String,
        reason: String,
        timeUtils : Long,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val hashMap : HashMap<String, String> = HashMap()
        hashMap["clientTeamName"] = clientTeamName
        hashMap["clientImageUrl"] = clientImageUrl
        hashMap["id"] = id
        hashMap["date"] = date
        hashMap["time"] = time
        hashMap["reason"] = reason
        hashMap["timeUtils"] = timeUtils.toString()

        firebaseDatabase.getReference("listNotifications").child(clientUID).push().setValue(hashMap)
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