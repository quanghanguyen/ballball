package com.example.ballball.main.chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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

    fun filter(
        userUID: String,
        text: String,
        onSuccess : (ArrayList<UsersModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Users").addValueEventListener(object :
        ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val filteredList = ArrayList<UsersModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(UsersModel::class.java)?.let {list ->
                            when {
                                list.teamName.lowercase(Locale.getDefault()).contains(text.lowercase(
                                    Locale.getDefault()))
                                        && userUID != list.userUid -> {
                                    filteredList.add(0, list)
                                }
                            }
                        }
                    }
                    onSuccess(filteredList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }
}