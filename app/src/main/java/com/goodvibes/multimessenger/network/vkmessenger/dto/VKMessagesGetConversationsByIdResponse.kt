package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesGetConversationsByIdResponse (
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<VKMessagesConversation>,
    @SerializedName("unread_count")
    val unreadCount: Int? = null,
    @SerializedName("profiles")
    val profiles: List<VKUserFull>? = null,
    @SerializedName("groups")
    val groups: List<VKGroupFull>? = null
)
