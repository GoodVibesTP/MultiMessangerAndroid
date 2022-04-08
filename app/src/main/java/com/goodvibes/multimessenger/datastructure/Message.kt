package com.goodvibes.multimessenger.datastructure

data class Message(
    var chatId: Int,
    var userId: Int,
    var text: String,
    var fwdMessages: List<Message>?,
    var replyTo: Message?,
    var messenger: Messengers
)
