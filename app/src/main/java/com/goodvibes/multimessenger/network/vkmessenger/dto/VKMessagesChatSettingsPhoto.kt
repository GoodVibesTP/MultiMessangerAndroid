package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesChatSettingsPhoto(
    @SerializedName("photo_50")
    val photo50: String? = null,
    @SerializedName("photo_100")
    val photo100: String? = null,
    @SerializedName("photo_200")
    val photo200: String? = null,
    @SerializedName("is_default_photo")
    val isDefaultPhoto: Boolean? = null,
    @SerializedName("is_default_call_photo")
    val isDefaultCallPhoto: Boolean? = null
)
