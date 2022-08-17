package com.example.ballball.main.contact

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.NewContactModel
import com.example.ballball.model.UsersModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ContactRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadContactList(
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

    fun loadNewContactList(
        userUID : String,
        onSuccess : (ArrayList<NewContactModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("newContact").child(userUID).addValueEventListener(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listRequest = ArrayList<NewContactModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(NewContactModel::class.java)?.let {list ->
                            listRequest.add(0, list)
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

    fun saveNewContact(
        userUID : String,
        name : String,
        phoneNumber : String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val newContact = NewContactModel(name, phoneNumber)
        firebaseDatabase.getReference("newContact").child(userUID).push().setValue(newContact)
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