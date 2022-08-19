package com.example.ballball.model

import com.google.gson.annotations.SerializedName

data class ListNotificationModel (
    @SerializedName("clientTeamName")
    val clientTeamName : String? = "",
    @SerializedName("clientUserImageUrl")
    val clientImageUrl : String? = "",
    @SerializedName("id")
    val id : String? = "",
    @SerializedName("date")
    val date : String? = "",
    @SerializedName("time")
    val time : String? = "",
    @SerializedName("reason")
    val reason : String = "",
    @SerializedName("timeUtils")
    val timeUtils : String = ""
        )