package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesAttachment(
    @SerializedName("type")
    val type: String,
    @SerializedName("photo")
    val photo: VKMessagesAttachmentPhoto?,
)
