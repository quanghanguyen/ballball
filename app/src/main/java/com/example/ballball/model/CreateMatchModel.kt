package com.example.ballball.model

import com.google.gson.annotations.SerializedName

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
    @SerializedName("click")
    val click : Int = 0,
    @SerializedName("clientUid1")
    var clientUID1 : String = "",
    @SerializedName("clientUid2")
    var clientUID2 : String = "",
    @SerializedName("clientUid3")
    var clientUID3 : String = ""
        )