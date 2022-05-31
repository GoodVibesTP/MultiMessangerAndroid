package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesMessage(
    @SerializedName("date")
    val date: Int,
    @SerializedName("from_id")
    val fromId: Long, // UserId
    @SerializedName("id")
    val id: Long,
//    @SerializedName("out")
//    val out: BaseBoolInt,
    @SerializedName("peer_id")
    val peerId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("attachments")
    val attachments: List<VKMessagesAttachment>? = null,
    @SerializedName("fwd_messages")
    val fwdMessages: List<VKMessagesMessage>? = null,
    @SerializedName("reply_message")
    val replyMessage: VKMessagesMessage? = null,
)
