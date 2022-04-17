package com.goodvibes.multimessenger.datastructure
import java.io.Serializable

data class Message(
    var id: Int,
    var chatId: Int,
    var userId: Int,
    var text: String,
    var fwdMessages: List<Message>?,
    var replyTo: Message?,
    var messenger: Messengers
) : Serializable
