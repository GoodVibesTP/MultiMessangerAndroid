package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesGetLongPoolServerResponse (
    @SerializedName("server")
    val server: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("ts")
    val ts: Int,
    @SerializedName("pts")
    val pts: Int
)
