package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesConversation(
    @SerializedName("peer")
    val peer: VKMessagesConversationPeer,
    @SerializedName("last_message_id")
    val lastMessageId: Long,
    @SerializedName("chat_settings")
    val chatSettings: VKMessagesChatSettings
)
