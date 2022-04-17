package com.goodvibes.multimessenger.usecase

import android.widget.Toast
import com.goodvibes.multimessenger.ChatActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message

class ChatActivityUC(_activityChat: ChatActivity) {
    var activityChat = _activityChat

    fun sendMessage(message: Message) {
        Toast.makeText(activityChat, "Send message "+ message.text, Toast.LENGTH_LONG).show()
    }

    fun getMessageFromChat(chat: Chat) : MutableList<Message> {
       val result = mutableListOf<Message>(
           Message(text = "Hello", isMyMessage = true),
           Message(text = "sfdjglsdjvjsdf;vsdfjv;sdf", isMyMessage = false),
           Message(text = "fgljsdvjknsfdv", isMyMessage = true),
           Message(text = "fvjslfdnfd;vnsdit;br;ohusdi;fvhjvbghfdkjshgljsdhljvb sfd", isMyMessage = false),
           Message(text = "sdfgjhvjsdfgntj;fsohgtrsjbvsfv;sdfjhgsgfbgsdflhjsgutbvsfdjghfsdg", isMyMessage = false),
           Message(text = "Hello", isMyMessage = true),
           Message(text = "Hello", isMyMessage = true)
       )

        return result
    }

}