package com.example.ballball.user.teaminformation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class TeamInformationRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase) {

    fun loadTeamInfo(
        userUID : String,
        onSuccess : (DataSnapshot) -> Unit,
        onFail : (Exception) -> Unit
    ) {
        firebaseDatabase.getReference("Teams").child(userUID).get()
            .addOnSuccessListener {
                onSuccess(it)
            }
            .addOnFailureListener {
                onFail(it)
            }
        }

    fun updateTeamInfo(
        teamUid : String,
        teamName: String,
        teamLocation: String,
        teamPeopleNumber: String,
        teamNote: String,
        deviceToken : String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val updateTeams = mapOf(
            "teamUid" to teamUid,
            "teamName" to teamName,
            "teamLocation" to teamLocation,
            "teamNote" to teamNote,
            "teamPeopleNumber" to teamPeopleNumber,
            "deviceToken" to deviceToken
        )

        firebaseDatabase.getReference("Teams").child(teamUid).updateChildren(updateTeams)
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