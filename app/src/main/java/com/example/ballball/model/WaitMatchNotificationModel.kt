package com.example.ballball.model

import com.google.gson.annotations.SerializedName

data class WaitMatchNotificationModel (
    @SerializedName("userUID")
    val userUID: String = "",
    @SerializedName("matchID")
    val matchID: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("clientTeamName")
    val clientTeamName: String = ""
        )