package com.example.ballball.main.home.nearme

import android.os.Build
import androidx.annotation.RequiresApi
import ch.hsr.geohash.GeoHash
import com.example.ballball.model.CreateMatchModel
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
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

class NearMeRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadNearMe(
        userUID : String,
        currentLat : Double,
        currentLong : Double,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").addValueEventListener(object :
        ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listNearMe = ArrayList<CreateMatchModel>()
                    for (nearMeSnapshot in snapshot.children) {
                        nearMeSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                            //geofences check
                            val locationGeoHash = GeoHash.fromGeohashString(list.geoHash)
                            val lat = locationGeoHash.originatingPoint.latitude
                            val long = locationGeoHash.originatingPoint.longitude
                            val destinationLocation = GeoLocation(lat, long)
                            val radiusInM = 2000.0
                            val myLocation = GeoLocation(currentLat, currentLong)
                            val distanceInM = GeoFireUtils.getDistanceBetween(myLocation, destinationLocation)

                            //date check
                            val currentDate = LocalDate.now()
                            val currentTime = LocalTime.now()
                            val matchDate = list.date
                            val matchTime = list.time
                            val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                            val timeFormatter = DateTimeFormatter.ofPattern("HH:m", Locale.ENGLISH)
                            val date = LocalDate.parse(matchDate, dateFormatter)
                            val time = LocalTime.parse(matchTime, timeFormatter)

                            when {
                                distanceInM <= radiusInM &&  userUID != list.userUID && date >= currentDate -> {
                                    listNearMe.add(0, list)
                                }
                            }
                        }
                    }
                    onSuccess(listNearMe)
                } else {
                    val listNearMe = ArrayList<CreateMatchModel>()
                    onSuccess(listNearMe)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }
}