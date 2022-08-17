package com.example.ballball.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersModel (
    @SerializedName("userUid")
    var userUid : String = "",
    @SerializedName("userName")
    var userName : String = "",
    @SerializedName("userPhone")
    var userPhone : String = "",
    @SerializedName("avatarUrl")
    var avatarUrl : String = "",
    @SerializedName("teamName")
    var teamName : String = ""
        ) : Parcelable