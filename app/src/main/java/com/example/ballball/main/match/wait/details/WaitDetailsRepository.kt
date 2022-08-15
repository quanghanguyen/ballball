package com.example.ballball.main.match.wait.details

import com.example.ballball.model.WaitMatchNotificationModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class WaitDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun cancelRequest(userUID: String, matchID: String,
                      onSuccess : (String) -> Unit,
                      onFail : (String) -> Unit)
    {
        firebaseDatabase.getReference("waitRequest").child(userUID).child(matchID).removeValue()
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

    fun cancelRequestNotification(
        userUID: String, matchID: String, date: String, time: String, clientTeamName: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val waitMatchNotificationModel = WaitMatchNotificationModel(userUID, matchID, date, time, clientTeamName)
        firebaseDatabase.getReference("cancelWaitRequest_Notification").child(userUID).child(matchID).setValue(waitMatchNotificationModel)
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
