package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKMessagesMessage(
    @SerializedName("date")
    val date: Int,
    @SerializedName("from_id")
    val fromId: Long, // UserId
    @SerializedName("id")
    val id: Long,
//    @SerializedName("out")
//    val out: BaseBoolInt,
    @SerializedName("peer_id")
    val peerId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("attachments")
    val attachments: List<Any>? = null,
    @SerializedName("fwd_messages")
    val fwdMessages: List<VKMessagesMessage>? = null,
    @SerializedName("reply_message")
    val replyMessage: VKMessagesMessage? = null,
//    @SerializedName("action")
//    val action: MessagesMessageAction? = null,
//    @SerializedName("admin_author_id")
//    val adminAuthorId: UserId? = null,
//    @SerializedName("attachments")
//    val attachments: List<MessagesMessageAttachment>? = null,
//    @SerializedName("conversation_message_id")
//    val conversationMessageId: Int? = null,
//    @SerializedName("deleted")
//    val deleted: BaseBoolInt? = null,
//    @SerializedName("geo")
//    val geo: BaseGeo? = null,
//    @SerializedName("important")
//    val important: Boolean? = null,
//    @SerializedName("is_hidden")
//    val isHidden: Boolean? = null,
//    @SerializedName("is_cropped")
//    val isCropped: Boolean? = null,
//    @SerializedName("keyboard")
//    val keyboard: MessagesKeyboard? = null,
//    @SerializedName("members_count")
//    val membersCount: Int? = null,
//    @SerializedName("payload")
//    val payload: String? = null,
//    @SerializedName("random_id")
//    val randomId: Int? = null,
//    @SerializedName("ref")
//    val ref: String? = null,
//    @SerializedName("ref_source")
//    val refSource: String? = null,
//    @SerializedName("reply_message")
//    val replyMessage: MessagesForeignMessage? = null,
//    @SerializedName("update_time")
//    val updateTime: Int? = null,
//    @SerializedName("was_listened")
//    val wasListened: Boolean? = null,
//    @SerializedName("pinned_at")
//    val pinnedAt: Int? = null,
//    @SerializedName("is_silent")
//    val isSilent: Boolean? = null
)
