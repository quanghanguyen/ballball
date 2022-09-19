package com.example.ballball.history

import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class HistoryRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadHistoryMatch(
        userUID : String,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("upComingMatch").child(userUID).addValueEventListener(object :
        ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val historyList = ArrayList<CreateMatchModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                            val currentDate = LocalDate.now()
                            val currentTime = LocalTime.now()
                            val matchDate = list.date
                            val matchTime = list.time
                            val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                            val timeFormatter = DateTimeFormatter.ofPattern("HH:m", Locale.ENGLISH)
                            val date = LocalDate.parse(matchDate, dateFormatter)
                            val time = LocalTime.parse(matchTime, timeFormatter)

                            if (
                                userUID != list.clientUID1 &&
                                userUID != list.clientUID2 &&
                                userUID != list.clientUID3 &&
                                date <= currentDate ) {
                                historyList.add(0, list)
                            }
                        }
                    }
                    onSuccess(historyList)
                } else {
                    val historyList = ArrayList<CreateMatchModel>()
                    onSuccess(historyList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }

    fun highlight(
        userUID : String,
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val highlight = mapOf(
            "highlight" to 1
        )

        firebaseDatabase.getReference("upComingMatch").child(userUID).child(matchID).updateChildren(highlight)
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
        userUID : String,
        matchID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val notHighLight = mapOf(
            "highlight" to 0
        )

        firebaseDatabase.getReference("upComingMatch").child(userUID).child(matchID).updateChildren(notHighLight)
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