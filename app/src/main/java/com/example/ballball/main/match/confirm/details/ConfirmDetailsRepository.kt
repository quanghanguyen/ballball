package com.example.ballball.main.match.confirm.details

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
        clientTeamName : String, clientImageUrl : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingMatch = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl)

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
    }