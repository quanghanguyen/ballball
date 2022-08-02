package com.example.ballball.main.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class HomeRepository @Inject constructor(private val firebaseStorage: FirebaseStorage) {
    fun loadAvatar(
        userUID : String,
        localFile : File,
        onSuccess : (Bitmap) -> Unit,
        onFail : (Exception) -> Unit
    ) {
        firebaseStorage.getReference("Users").child(userUID).getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                onSuccess(bitmap)
            }
            .addOnFailureListener{
                onFail(it)
            }
        }
    }