package com.example.ballball.user.walkthrough.name

import com.example.ballball.model.UsersModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class NameRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun saveUser(
        userUid: String,
        userName: String,
        userPhone: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val users = UsersModel(userUid, userName, userPhone)
        firebaseDatabase.getReference("Users").child(userUid).setValue(users)
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