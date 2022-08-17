package com.example.ballball.user.walkthrough.team

import android.net.Uri
import com.example.ballball.model.TeamsModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class TeamRepository @Inject constructor (
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) {
    fun saveTeam(
        teamUid: String,
        teamName: String,
        teamLocation: String,
        teamPeopleNumber: String,
        teamNote: String,
        deviceToken : String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val teams = TeamsModel(teamUid, teamName, teamLocation, teamPeopleNumber, teamNote, deviceToken)
        firebaseDatabase.getReference("Teams").child(teamUid).setValue(teams)
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

    fun saveTeamImage(
        imgUri : Uri,
        teamUid : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseStorage.getReference("Teams/$teamUid")
            .putFile(imgUri)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener{
                onFail(it.message.orEmpty())
            }
        }

    fun updateUser(
        userUID : String,
        teamName : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val updateUsers = mapOf(
            "teamName" to teamName
        )
        firebaseDatabase.getReference("Users").child(userUID).updateChildren(updateUsers)
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