package com.example.ballball.listnotification

import com.example.ballball.model.ListNotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ListNotificationRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadNotificationList(
        userUID : String,
        onSuccess : (ArrayList<ListNotificationModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("listNotifications").child(userUID).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listNotification = ArrayList<ListNotificationModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(ListNotificationModel::class.java)?.let {
                            listNotification.add(0, it)
                        }
                    }
                    onSuccess(listNotification)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }

    fun markRead(
        userUID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        firebaseDatabase.getReference("listNotifications").child(userUID).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.toString())
            }
        }
    }