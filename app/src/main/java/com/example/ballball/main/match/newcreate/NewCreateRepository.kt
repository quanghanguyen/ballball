package com.example.ballball.main.match.newcreate

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

class NewCreateRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadNewCreate(
        userUID: String,
        onSuccess: (ArrayList<CreateMatchModel>) -> Unit,
        onFail: (String) -> Unit
    ) {
        firebaseDatabase.getReference("New_Create_Match").child(userUID).addValueEventListener(object:
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listRequest = ArrayList<CreateMatchModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(CreateMatchModel::class.java)?.let { list ->
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
}