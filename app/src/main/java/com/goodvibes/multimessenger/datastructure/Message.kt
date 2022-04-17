package com.goodvibes.multimessenger.datastructure

data class Message(
    var id: Long,
    var chatId: Long,
    var userId: Long,
    var text: String,
    var date: Int,
    var fwdMessages: List<Message>?,
    var replyTo: Message?,
    var messenger: Messengers
)
