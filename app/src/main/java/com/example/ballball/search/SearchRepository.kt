package com.example.ballball.search

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SearchRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun filter(
        userUID : String,
        text : String,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").addValueEventListener(object:
        ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val filteredList = ArrayList<CreateMatchModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(CreateMatchModel::class.java)?.let { list ->
                            val currentDate = LocalDate.now()
                            val currentTime = LocalTime.now()
                            val matchDate = list.date
                            val matchTime = list.time
                            val dateFormatter =
                                DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                            val timeFormatter = DateTimeFormatter.ofPattern("HH:m", Locale.ENGLISH)
                            val date = LocalDate.parse(matchDate, dateFormatter)
                            val time = LocalTime.parse(matchTime, timeFormatter)

                            if (userUID != list.userUID &&
                                userUID != list.clientUID1 &&
                                userUID != list.clientUID2 &&
                                userUID != list.clientUID3 &&
                                date >= currentDate
                            ) {
                                if (list.teamName.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.date.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.location.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.teamPeopleNumber.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.teamPhone.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.time.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.date.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.date.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())) ||
                                    list.locationAddress.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault())))
                                {
                                    filteredList.add(list)
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