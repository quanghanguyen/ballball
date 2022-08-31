package com.example.ballball.main.match.upcoming.details

import com.example.ballball.model.CancelUpComingModel
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

    fun restoreMatch(

    ) {

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