package com.goodvibes.multimessenger.usecase

import android.widget.Toast
import com.goodvibes.multimessenger.ChatActivity
import com.goodvibes.multimessenger.datastructure.Message

class ChatActivityUC(_activityChat: ChatActivity) {
    var activityChat = _activityChat
    fun sendMessage(message: Message) {
        Toast.makeText(activityChat, "Send message "+ message.text, Toast.LENGTH_LONG).show()
    }
}