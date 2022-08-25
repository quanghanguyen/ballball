package com.example.ballball.main.match.confirm

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ConfirmRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadConfirmList(
        userUID : String,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("confirmRequest").child(userUID).addValueEventListener(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listRequest = ArrayList<CreateMatchModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                            val current = LocalDate.now()
                            val matchTime = list.date
                            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                            val date = LocalDate.parse(matchTime, formatter)
                            when {
                                date >= current -> {
                                    listRequest.add(0, list)
                                }
                            }
                        }
                    }
                    onSuccess(listRequest)
                } else {
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
        userUID : String,
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val highlight = mapOf(
            "highlight" to 1
        )

        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).updateChildren(highlight)
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

        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).updateChildren(notHighLight)
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