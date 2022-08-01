package com.example.ballball.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TeamsModel (
    @SerializedName("teamUid")
    var teamUid: String = "",
    @SerializedName("teamName")
    var teamName: String = "",
    @SerializedName("teamLocation")
    var teamLocation: String = "",
    @SerializedName("teamPeopleNumber")
    var teamPeopleNumber : String = "",
    @SerializedName("teamNote")
    var teamNote : String
        ) : Parcelable