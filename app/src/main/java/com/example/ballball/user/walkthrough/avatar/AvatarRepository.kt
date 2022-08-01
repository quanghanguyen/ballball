package com.example.ballball.user.walkthrough.avatar

import android.net.Uri
import com.example.ballball.utils.AuthConnection.uid
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class AvatarRepository @Inject constructor(private val firebaseStorage: FirebaseStorage) {
    fun saveAvatar(
        imgUri : Uri,
        userUID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseStorage.getReference("Users/$userUID")
            .putFile(imgUri)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener{
                onFail(it.message.orEmpty())
            }
        }
    }