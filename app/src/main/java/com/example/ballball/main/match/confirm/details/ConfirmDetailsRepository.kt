package com.example.ballball.main.match.confirm.details

import com.example.ballball.model.AcceptMatchNotification
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class ConfirmDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun denyMatch(
        userUID : String,
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).removeValue()
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

    fun upComingMatch (
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingMatch = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl, confirmUID, clientUID)

        firebaseDatabase.getReference("upComingMatch").child(userUID).child(matchID).setValue(upComingMatch)
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

    fun deleteConfirmMatch(
        userUID: String,
        matchID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).removeValue()
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

    fun upComingMatchClient(
        confirmUID : String, userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, clientUID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingMatchClient = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl, confirmUID, clientUID)

        firebaseDatabase.getReference("upComingMatch").child(confirmUID).child(matchID).setValue(upComingMatchClient)
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

    fun deleteWaitMatch(
        confirmUID : String,
        matchID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        firebaseDatabase.getReference("waitRequest").child(confirmUID).child(matchID).removeValue()
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

    fun acceptRequestNotification(
        clientUID: String, userUID: String, matchID: String, date: String, time: String, teamName: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val acceptMatchNotification = AcceptMatchNotification(clientUID, userUID, matchID, date, time, teamName)
        firebaseDatabase.getReference("acceptRequest_Notification").child(userUID).child(matchID).setValue(acceptMatchNotification)
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

    fun denyRequestNotification(
        clientUID: String, userUID: String, matchID: String, date: String, time: String, teamName: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val acceptMatchNotification = AcceptMatchNotification(clientUID, userUID, matchID, date, time, teamName)
        firebaseDatabase.getReference("denyRequest_Notification").child(userUID).child(matchID).setValue(acceptMatchNotification)
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