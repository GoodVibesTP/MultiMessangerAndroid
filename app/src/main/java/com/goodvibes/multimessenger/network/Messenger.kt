package com.goodvibes.multimessenger.network

import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers

interface Messenger {
    val messenger: Messengers

    fun getAllChats(count: Int, first_msg: Int = 0, callback: (List<Chat>) -> Unit): List<Chat>
    fun getMessagesFromChat(chat_id: Int, count: Int, first_msg: Int = 0): List<Message>
    fun sendMessage(user_id: Int, text: String)
    fun authorize()
}
