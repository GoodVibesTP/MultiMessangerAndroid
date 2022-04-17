package com.goodvibes.multimessenger.datastructure
import java.io.Serializable

const val idMessageDefault = 0
const val chatMessageDefault = 0
const val userIdMessageDefault = 0


data class Message(
    var id: Int = idMessageDefault,
    var chatId: Int = chatMessageDefault,
    var userId: Int = userIdMessageDefault,
    var text: String,
    var fwdMessages: List<Message>? = null,
    var replyTo: Message? = null,
    var messenger: Messengers? = null,
) : Serializable
