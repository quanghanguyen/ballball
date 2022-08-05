package com.example.ballball.main.home.all

import android.util.Log
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class AllRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadMatchList (
        userUID : String,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                   val listRequest = ArrayList<CreateMatchModel>()
                   for (requestSnapshot in snapshot.children) {
                       requestSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                           when {
                               userUID != list.clientUID1 && userUID != list.clientUID2 && userUID != list.clientUID3 -> {
                                   listRequest.add(0, list)
                               }
                               else -> {
                                   Log.e("Error", "Đã xảy ra lỗi")
                               }
                           }
                       }
                   }
                    onSuccess(listRequest)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }
}