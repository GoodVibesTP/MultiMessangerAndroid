package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesConversation(
    @SerializedName("peer")
    val peer: VKMessagesConversationPeer,
    @SerializedName("last_message_id")
    val lastMessageId: Long,
    @SerializedName("in_read")
    val inRead: Long,
    @SerializedName("out_read")
    val outRead: Long,
    @SerializedName("unread_count")
    val unreadCount: Int,
    @SerializedName("chat_settings")
    val chatSettings: VKMessagesChatSettings
)
