package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesChatSettings(
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("title")
    val title: String
//    @SerializedName("state")
//    val state: MessagesChatSettingsState,
//    @SerializedName("active_ids")
//    val activeIds: List<UserId>,
//    @SerializedName("members_count")
//    val membersCount: Int? = null,
//    @SerializedName("friends_count")
//    val friendsCount: Int? = null,
//    @SerializedName("pinned_message")
//    val pinnedMessage: MessagesPinnedMessage? = null,
//    @SerializedName("photo")
//    val photo: MessagesChatSettingsPhoto? = null
)
