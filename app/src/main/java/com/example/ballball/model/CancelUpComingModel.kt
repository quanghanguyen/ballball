package com.example.ballball.model

import com.google.gson.annotations.SerializedName

data class CancelUpComingModel (
    @SerializedName("clientUID")
    val clientUID : String,
    @SerializedName("userUID")
    val userUID: String = "",
    @SerializedName("matchID")
    val matchID: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("teamName")
    val teamName: String = "",
    @SerializedName("reason")
    val reason: String = ""
        )