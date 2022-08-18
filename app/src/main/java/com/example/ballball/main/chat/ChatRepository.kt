package com.example.ballball.main.chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.UsersModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadChatChannel(
        userUID : String,
        onSuccess : (ArrayList<UsersModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Users").addValueEventListener(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listRequest = ArrayList<UsersModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(UsersModel::class.java)?.let {list ->
                            when {
                                userUID != list.userUid -> {
                                    listRequest.add(0, list)
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