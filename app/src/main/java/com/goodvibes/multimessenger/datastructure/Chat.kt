package com.goodvibes.multimessenger.datastructure

import java.io.Serializable

data class Chat(
    var chatId: Int,
    var img: Int,
    var imgUri: String?,
    var title: String,
    var lastMessage: Message?,
    var chatType: ChatType,
    var messenger: Messengers,
    var folder: Folder = Folder(-100,"empty",),
) : Serializable

