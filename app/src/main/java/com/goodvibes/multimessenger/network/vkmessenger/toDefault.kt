package com.goodvibes.multimessenger.network.vkmessenger

import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.ChatType
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.vkmessenger.dto.VKMessagesConversationPeerType
import com.goodvibes.multimessenger.network.vkmessenger.dto.VKMessagesConversationWithMessage
import com.goodvibes.multimessenger.network.vkmessenger.dto.VKMessagesGetConversationsResponse
import com.goodvibes.multimessenger.network.vkmessenger.dto.VKMessagesMessage

fun toDefaultChat(
    conversationWithMessage: VKMessagesConversationWithMessage,
    response: VKMessagesGetConversationsResponse
) : Chat {
    return Chat(
        chatId = conversationWithMessage.conversation.peer.id,
        img = 0,
        title = when(conversationWithMessage.conversation.peer.type) {
            VKMessagesConversationPeerType.CHAT -> {
                conversationWithMessage.conversation.chatSettings.title
            }
            VKMessagesConversationPeerType.USER -> {
                val profile = response.profiles?.firstOrNull {
                    it.id == conversationWithMessage.conversation.peer.id
                }
                "${profile?.firstName} ${profile?.lastName}"
            }
            VKMessagesConversationPeerType.GROUP -> {
                val group = response.groups?.firstOrNull {
                    it.id == - conversationWithMessage.conversation.peer.id
                }
                "${group?.name}"
            }
            else -> ""
        },
        chatType = when(conversationWithMessage.conversation.peer.type) {
            VKMessagesConversationPeerType.CHAT -> ChatType.CHAT
            VKMessagesConversationPeerType.USER -> ChatType.USER
            VKMessagesConversationPeerType.GROUP -> ChatType.GROUP
            else -> ChatType.OTHER
        },
        messenger = Messengers.VK,
        lastMessage = toDefaultMessage(conversationWithMessage.lastMessage)
    )
}

fun toDefaultMessage(
    message: VKMessagesMessage?
) : Message? {
    if (message == null) {
        return null
    }
    var fwdMessages: ArrayList<Message>? = null
    if (message.fwdMessages != null) {
        fwdMessages = arrayListOf()
        fwdMessages.ensureCapacity(message.fwdMessages.size)
        for (msg in message.fwdMessages) {
            fwdMessages.add(toDefaultMessage(msg)!!)
        }
    }
    return Message(
        chatId = message.peerId,
        userId = message.fromId,
        text = message.text,
        fwdMessages = fwdMessages,
        replyTo = toDefaultMessage(message.replyMessage),
        messenger = Messengers.VK
    )
}