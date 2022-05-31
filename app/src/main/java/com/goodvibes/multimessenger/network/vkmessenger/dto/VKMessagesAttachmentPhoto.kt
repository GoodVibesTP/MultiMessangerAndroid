package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesAttachmentPhoto(
    @SerializedName("date")
    val date: Int,
    @SerializedName("id")
    val id: Long,
    @SerializedName("owner_id")
    val owner_id: Long,
    @SerializedName("access_key")
    val access_key: String,
    @SerializedName("sizes")
    val sizes: List<VKMessagesAttachmentPhotoSize>
)
