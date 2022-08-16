package com.example.ballball.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateMatchModel (
    @SerializedName("userUID")
    val userUID : String = "",
    @SerializedName("matchID")
    val matchID : String = "",
    @SerializedName("deviceToken")
    val deviceToken : String = "",
    @SerializedName("teamName")
    val teamName : String = "",
    @SerializedName("teamPhone")
    val teamPhone : String = "",
    @SerializedName("date")
    val date : String = "",
    @SerializedName("time")
    val time : String = "",
    @SerializedName("location")
    val location : String = "",
    @SerializedName("note")
    val note : String = "",
    @SerializedName("teamPeopleNumber")
    val teamPeopleNumber : String = "",
    @SerializedName("teamImageUrl")
    val teamImageUrl : String = "",
    @SerializedName("locationAddress")
    val locationAddress : String = "",
    @SerializedName("lat")
    val lat : Double = 1.01,
    @SerializedName("long")
    val long : Double = 1.01,
    @SerializedName("click")
    val click : Int = 0,
    @SerializedName("clientTeamName")
    val clientTeamName : String = "",
    @SerializedName("clientImageUrl")
    val clientImageUrl : String = "",
    @SerializedName("confirmUID")
    val confirmUID : String = "",
    @SerializedName("clientUID")
    val clientUID : String = "",
    @SerializedName("clientUid")
    var clientUID1 : String = "",
    @SerializedName("clientUid2")
    var clientUID2 : String = "",
    @SerializedName("clientUid3")
    var clientUID3 : String = ""
        ) : Parcelable