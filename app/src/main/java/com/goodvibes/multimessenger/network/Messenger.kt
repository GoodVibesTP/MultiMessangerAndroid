package com.goodvibes.multimessenger.network

import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers

interface Messenger {
    val messenger: Messengers

    fun isAuthorized(): Boolean

    fun getUserId(): Long

    fun getAllChats(
        count: Int,
        first_chat: Int = 0,
        callback: (MutableList<Chat>) -> Unit
    )

    fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        offset: Int = 0,
        first_msg_id: Long = 0,
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

    fun deleteMessages(
        chat_id: Long,
        message_ids: List<Long>,
        callback: (List<Int>) -> Unit = { }
    )

    fun markAsRead(
        peer_id: Long,
        message_ids: List<Long>?,
        start_message_id: Long?,
        mark_conversation_as_read: Boolean = false,
        callback: (Int) -> Unit = { }
    )

    fun startUpdateListener(
        callback: (Event) -> Unit = { }
    )

    fun authorize()
}
