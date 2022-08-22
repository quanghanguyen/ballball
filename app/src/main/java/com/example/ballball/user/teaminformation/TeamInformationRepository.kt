package com.example.ballball.user.teaminformation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class TeamInformationRepository @Inject constructor(
//    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase) {

//    fun loadTeamImage(
//        userUID : String,
//        localFile : File,
//        onSuccess : (Bitmap) -> Unit,
//        onFail : (Exception) -> Unit
//    ) {
//        firebaseStorage.getReference("Teams").child(userUID).getFile(localFile)
//            .addOnSuccessListener {
//                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//                onSuccess(bitmap)
//            }
//            .addOnFailureListener{
//                onFail(it)
//            }
//        }

    fun loadTeamInfo(
        userUID : String,
        onSuccess : (DataSnapshot) -> Unit,
        onFail : (Exception) -> Unit
    ) {
        firebaseDatabase.getReference("Teams").child(userUID).get()
            .addOnSuccessListener {
                onSuccess(it)
            }
            .addOnFailureListener {
                onFail(it)
            }
        }
    }