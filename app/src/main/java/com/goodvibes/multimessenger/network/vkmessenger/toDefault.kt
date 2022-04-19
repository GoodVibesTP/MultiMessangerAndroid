package com.goodvibes.multimessenger.network.vkmessenger

import android.annotation.SuppressLint
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.ChatType
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.dto.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
private val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale("ru", "ru"))

fun toDefaultChat(
    conversationWithMessage: VKMessagesConversationWithMessage,
    response: VKMessagesGetConversationsResponse,
    currentUserId: Long
) : Chat {
    return Chat(
        chatId = conversationWithMessage.conversation.peer.id.toLong(),
        img = R.drawable.kotik,
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
        lastMessage = toDefaultMessage(conversationWithMessage.lastMessage, currentUserId)
    )
}


fun toDefaultChat(
    conversationWithMessage: VKMessagesConversationWithMessage,
    response: VKMessagesGetConversationsByIdResponse,
    currentUserId: Long
) : Chat {
    return Chat(
        chatId = conversationWithMessage.conversation.peer.id,
        img = R.drawable.kotik,
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
        lastMessage = toDefaultMessage(conversationWithMessage.lastMessage, currentUserId)
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
        time = dateFormat.format(message.date * 1000L),
        isMyMessage = currentUserId == message.fromId,
        fwdMessages = fwdMessages,
        replyTo = toDefaultMessage(message.replyMessage, currentUserId),
        messenger = Messengers.VK
    )
}
