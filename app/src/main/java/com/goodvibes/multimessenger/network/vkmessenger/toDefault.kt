package com.goodvibes.multimessenger.network.vkmessenger

import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.ChatType
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.vkmessenger.dto.*
import java.util.*
import kotlin.collections.ArrayList

fun toDefaultChat(
    conversationWithMessage: VKMessagesConversationWithMessage,
    response: VKMessagesGetConversationsResponse,
    currentUserId: Long
) : Chat {
    val lastMessage = toDefaultMessage(conversationWithMessage.lastMessage, currentUserId)
    if (lastMessage != null) {
        lastMessage.read = if (lastMessage.isMyMessage) {
            lastMessage.id <= conversationWithMessage.conversation.outRead
        }
        else {
            lastMessage.id <= conversationWithMessage.conversation.inRead
        }
    }
    return Chat(
        chatId = conversationWithMessage.conversation.peer.id,
        img = R.mipmap.tg_icon,
        imgUri = when(conversationWithMessage.conversation.peer.type) {
            VKMessagesConversationPeerType.CHAT -> {
                conversationWithMessage.conversation.chatSettings.photo?.photo200
            }
            VKMessagesConversationPeerType.USER -> {
                val profile = response.profiles?.firstOrNull {
                    it.id == conversationWithMessage.conversation.peer.id
                }
                profile?.photo200
            }
            VKMessagesConversationPeerType.GROUP -> {
                val group = response.groups?.firstOrNull {
                    it.id == -conversationWithMessage.conversation.peer.id
                }
                group?.photo200
            }
            else -> null
        },
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
        lastMessage = lastMessage,
        inRead = conversationWithMessage.conversation.inRead,
        outRead = conversationWithMessage.conversation.outRead,
        unreadMessage = conversationWithMessage.conversation.unreadCount
    )
}


fun toDefaultChat(
    conversationWithMessage: VKMessagesConversationWithMessage,
    response: VKMessagesGetConversationsByIdResponse,
    currentUserId: Long
) : Chat {
    val lastMessage = toDefaultMessage(conversationWithMessage.lastMessage, currentUserId)
    if (lastMessage != null) {
        lastMessage.read = if (lastMessage.isMyMessage) {
            lastMessage.id <= conversationWithMessage.conversation.outRead
        }
        else {
            lastMessage.id <= conversationWithMessage.conversation.inRead
        }
    }
    return Chat(
        chatId = conversationWithMessage.conversation.peer.id,
        img = R.mipmap.tg_icon,
        imgUri = when(conversationWithMessage.conversation.peer.type) {
            VKMessagesConversationPeerType.CHAT -> {
                conversationWithMessage.conversation.chatSettings.photo?.photo200
            }
            VKMessagesConversationPeerType.USER -> {
                val profile = response.profiles?.firstOrNull {
                    it.id == conversationWithMessage.conversation.peer.id
                }
                profile?.photo200
            }
            VKMessagesConversationPeerType.GROUP -> {
                val group = response.groups?.firstOrNull {
                    it.id == -conversationWithMessage.conversation.peer.id
                }
                group?.photo200
            }
            else -> null
        },
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
        lastMessage = lastMessage,
        inRead = conversationWithMessage.conversation.inRead,
        outRead = conversationWithMessage.conversation.outRead,
        unreadMessage = conversationWithMessage.conversation.unreadCount
    )
}


fun toDefaultMessage(
    message: VKMessagesMessage?,
    currentUserId: Long
) : Message? {
    if (message == null) {
        return null
    }
    var fwdMessages: ArrayList<Message>? = null
    if (message.fwdMessages != null) {
        fwdMessages = arrayListOf()
        fwdMessages.ensureCapacity(message.fwdMessages.size)
        for (msg in message.fwdMessages) {
            fwdMessages.add(toDefaultMessage(msg, currentUserId)!!)
        }
    }
    return Message(
        id = message.id,
        chatId = message.peerId,
        userId = message.fromId,
        text = message.text,
        date = message.date,
        time = VK.dateFormat.format(message.date * 1000L),
        isMyMessage = currentUserId == message.fromId,
        fwdMessages = fwdMessages,
        replyTo = toDefaultMessage(message.replyMessage, currentUserId),
        messenger = Messengers.VK
    )
}
