package com.example.ballball.main.home.all

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AllRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadMatchList (
        userUID : String,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").addValueEventListener(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                   val listRequest = ArrayList<CreateMatchModel>()
                   for (requestSnapshot in snapshot.children) {
                       val childName = requestSnapshot.key.toString()
                       requestSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                           val currentDate = LocalDate.now()
                           val currentTime = LocalTime.now()
                           val matchDate = list.date
                           val matchTime = list.time
                           val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                           val timeFormatter = DateTimeFormatter.ofPattern("HH:m", Locale.ENGLISH)
                           val date = LocalDate.parse(matchDate, dateFormatter)
                           val time = LocalTime.parse(matchTime, timeFormatter)

                           if (userUID != list.userUID &&
//                               date >= currentDate &&
                               userUID != list.clientUID1 &&
                               userUID != list.clientUID2 &&
                               userUID != list.clientUID3
                               ) {
                                   listRequest.add(0, list)
                           }
                       }
                   }
                    onSuccess(listRequest)
                }
                else {
                    val listRequest = ArrayList<CreateMatchModel>()
                    onSuccess(listRequest)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }

    fun highlight(
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val highlight = mapOf(
            "highlight" to 1
        )

        firebaseDatabase.getReference("Request_Match").child(matchID).updateChildren(highlight)
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

    fun notHighLight(
        matchID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val notHighLight = mapOf(
            "highlight" to 0
        )

        firebaseDatabase.getReference("Request_Match").child(matchID).updateChildren(notHighLight)
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