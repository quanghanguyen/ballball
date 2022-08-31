package com.example.ballball.model

import com.google.gson.annotations.SerializedName

data class AllNotificationModel (
    @SerializedName("matchId")
    val matchId : String = "",
    @SerializedName("teamName")
    val teamName : String = "",
    @SerializedName("userUID")
    val userUID : String = ""
        )