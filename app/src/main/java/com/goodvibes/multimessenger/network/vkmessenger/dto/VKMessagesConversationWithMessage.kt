package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesConversationWithMessage(
    @SerializedName("conversation")
    val conversation: VKMessagesConversation,
    @SerializedName("last_message")
    val lastMessage: VKMessagesMessage? = null
)
