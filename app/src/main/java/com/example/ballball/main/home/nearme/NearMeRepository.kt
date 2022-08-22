package com.example.ballball.main.home.nearme

import ch.hsr.geohash.GeoHash
import com.example.ballball.model.CreateMatchModel
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class NearMeRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadNearMe(
        currentLat : Double,
        currentLong : Double,
        onSuccess : (ArrayList<CreateMatchModel>) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").addValueEventListener(object :
        ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listNearMe = ArrayList<CreateMatchModel>()
                    for (nearMeSnapshot in snapshot.children) {
                        nearMeSnapshot.getValue(CreateMatchModel::class.java)?.let {list ->
                            val locationGeoHash = GeoHash.fromGeohashString(list.geoHash)
                            val lat = locationGeoHash.originatingPoint.latitude
                            val long = locationGeoHash.originatingPoint.longitude
                            val destinationLocation = GeoLocation(lat, long)
                            val radiusInM = 2000.0
                            val myLocation = GeoLocation(currentLat, currentLong)
                            val distanceInM = GeoFireUtils.getDistanceBetween(myLocation, destinationLocation)
                            when {
                                distanceInM <= radiusInM -> {
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