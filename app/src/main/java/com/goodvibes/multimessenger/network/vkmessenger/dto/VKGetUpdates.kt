package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

class VKGetUpdates (
    @SerializedName("ts")
    val ts: Int?,
    @SerializedName("updates")
    val updates: List<List<JsonElement>>
)
