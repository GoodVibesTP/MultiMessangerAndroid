package com.goodvibes.multimessenger.datastructure
import java.io.Serializable

const val idMessageDefault = 0L
const val chatMessageDefault = 0L
const val userIdMessageDefault = 0L
const val dateMessageDefault = 0


data class Message(
    var id: Long = idMessageDefault,
    var chatId: Long = chatMessageDefault,
    var userId: Long = userIdMessageDefault,
    var text: String,
    var fwdMessages: List<Message>? = null,
    var replyTo: Message? = null,
    var messenger: Messengers? = null,
    var isMyMessage: Boolean = false,
    var date: Int = dateMessageDefault,
    var time: String = "21:00"
) : Serializable
