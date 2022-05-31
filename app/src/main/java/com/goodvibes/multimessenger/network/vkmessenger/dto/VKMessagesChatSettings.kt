package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesChatSettings(
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("photo")
    val photo: VKMessagesChatSettingsPhoto? = null
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
)
