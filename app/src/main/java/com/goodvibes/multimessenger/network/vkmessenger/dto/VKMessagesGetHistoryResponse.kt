package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesGetHistoryResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<VKMessagesMessage>,
    @SerializedName("conversations")
    val conversations: List<VKMessagesConversation>,
    @SerializedName("profiles")
    val profiles: List<VKUserFull>? = null
//    @SerializedName("groups")
//    val groups: List<GroupsGroupFull>? = null
)
