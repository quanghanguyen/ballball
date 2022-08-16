package com.example.ballball.main.home.all.details

import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.WaitMatchNotificationModel
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AllDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    @Exclude
    fun catchMatch (
        matchId : String,
        uid : String,
        clientUid: String,
        click : Int,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val user = mapOf(
            clientUid to uid,
            "click" to click
        )

        firebaseDatabase.getReference("Request_Match").child(matchId).updateChildren(user)
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

    fun waitMatch(
        uID: String, userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val waitData = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl)
        firebaseDatabase.getReference("waitRequest").child(uID).child(matchID).setValue(waitData)
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

    fun waitMatchNotification(
        userUID: String, matchID: String, date: String, time: String, clientTeamName: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val waitMatchNotificationModel = WaitMatchNotificationModel(userUID, matchID, date, time, clientTeamName)
        firebaseDatabase.getReference("waitRequest_Notification").child(userUID).child(matchID).setValue(waitMatchNotificationModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
        }

    fun confirmMatch(
        waitUID: String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, teamWaitUID : String, clientUID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val confirmMatch = CreateMatchModel(waitUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl, teamWaitUID, clientUID)

        firebaseDatabase.getReference("confirmRequest").child(waitUID).child(matchID).setValue(confirmMatch)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                }
                else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
        }
    }