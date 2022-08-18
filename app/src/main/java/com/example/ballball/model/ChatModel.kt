package com.example.ballball.model

import com.google.gson.annotations.SerializedName


data class ChatModel(
    @SerializedName("senderId")
    val senderId : String? = "",
    @SerializedName("receivedId")
    val receiverId : String? = "",
    @SerializedName("message")
    val message : String? = "",
    @SerializedName("time")
    val time : String? = "",

)