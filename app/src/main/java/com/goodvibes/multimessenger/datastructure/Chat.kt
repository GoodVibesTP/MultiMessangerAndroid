package com.goodvibes.multimessenger.datastructure

import android.graphics.Bitmap
import org.drinkless.td.libcore.telegram.TdApi
import java.io.Serializable

data class Chat(
    var chatId: Long,
    var img: Int,
    var imgUri: String?,
    var title: String,
    var lastMessage: Message?,
    var chatType: ChatType,
    var messenger: Messengers,
    var folder: Folder = Folder(-100,"empty",),
    var inRead: Long,
    var outRead: Long,
    var unreadMessage: Int?,
) : Serializable
