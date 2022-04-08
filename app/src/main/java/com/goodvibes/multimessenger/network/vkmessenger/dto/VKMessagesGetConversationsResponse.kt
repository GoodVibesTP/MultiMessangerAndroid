package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesGetConversationsResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<VKMessagesConversationWithMessage>,
    @SerializedName("unread_count")
    val unreadCount: Int? = null,
    @SerializedName("profiles")
    val profiles: List<VKUserFull>? = null,
    @SerializedName("groups")
    val groups: List<VKGroupFull>? = null
)
