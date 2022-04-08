package com.goodvibes.multimessenger.datastructure


data class Chat(
    var chatId: Int,
    var img: Int,
    var title: String,
    var lastMessage: Message?,
    var chatType: ChatType,
    var messenger: Messengers
)

