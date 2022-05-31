package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesConversationPeer(
    @SerializedName("id")
    val id: Long,
    @SerializedName("type")
    val type: VKMessagesConversationPeerType,
    @SerializedName("local_id")
    val localId: Int? = null
)
