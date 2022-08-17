package com.example.ballball.model

import com.google.gson.annotations.SerializedName

data class NewContactModel (
    @SerializedName("name")
    val name : String = "",
    @SerializedName("phoneNumber")
    val phoneNumber : String = ""
        )