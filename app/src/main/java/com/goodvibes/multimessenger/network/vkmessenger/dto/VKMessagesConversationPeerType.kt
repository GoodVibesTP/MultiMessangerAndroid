package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

enum class VKMessagesConversationPeerType(
    val value: String
) {
    @SerializedName("chat")
    CHAT("chat"),

    @SerializedName("email")
    EMAIL("email"),

    @SerializedName("user")
    USER("user"),

    @SerializedName("contact")
    CONTACT("contact"),

    @SerializedName("group")
    GROUP("group");
}
