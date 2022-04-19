package com.goodvibes.multimessenger.network

import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers

interface Messenger {
    val messenger: Messengers

    fun isAuthorized(): Boolean

    fun getAllChats(
        count: Int,
        first_chat: Int = 0,
        callback: (MutableList<Chat>) -> Unit
    )

    fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        first_msg: Int = 0,
        callback: (MutableList<Message>) -> Unit
    )

    fun getChatById(
        chat_id: Long,
        callback: (Chat) -> Unit
    )

    fun sendMessage(
        user_id: Long,
        text: String,
        callback: (Long) -> Unit = { }
    )

    fun startUpdateListener(
        callback: (Event) -> Unit = { }
    )

    fun authorize()
}
