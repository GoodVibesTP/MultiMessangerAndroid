package com.goodvibes.multimessenger.usecase

import com.goodvibes.multimessenger.ChatActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.tgmessenger.Telegram
import com.goodvibes.multimessenger.network.vkmessenger.VK

class ChatActivityUC(_activityChat: ChatActivity) {
    var activityChat = _activityChat
    val tg = Telegram
    val vk = VK

    fun sendMessage(message: Message, callback: (Long) -> Unit) {
        when(message.messenger) {
            Messengers.VK -> {
                vk.sendMessage(message.chatId, message.text, callback)
            }
            Messengers.TELEGRAM -> {
                tg.sendMessage(message.chatId, message.text, callback)
            }
        }
    }

    fun editMessage(message: Message, callback: (Long) -> Unit = { }) {
        when(message.messenger) {
            Messengers.VK -> {
                vk.editMessage(message.chatId, message.id, message.text, callback)
            }
            Messengers.TELEGRAM -> {
                tg.editMessage(message.chatId, message.id, message.text, callback)
            }
        }
    }

    fun deleteMessages(chat: Chat, message_ids: List<Long>, callback: (List<Int>) -> Unit = { }) {
        when(chat.messenger) {
            Messengers.VK -> {
                vk.deleteMessages(chat.chatId, message_ids, callback)
            }
            Messengers.TELEGRAM -> {
                tg.deleteMessages(chat.chatId, message_ids, callback)
            }
        }
    }

    fun getMessageFromChat(chat: Chat, count: Int, first_msg_id: Long = 0, callback: (MutableList<Message>) -> Unit) {
        var idLastMessage: Long
        if (chat.lastMessage == null) {
            idLastMessage = -1
        } else {
            idLastMessage = if (first_msg_id != 0L) first_msg_id else chat.lastMessage!!.id
        }

        when(chat.messenger) {
            Messengers.VK -> {
                vk.getMessagesFromChat(
                    chat.chatId,
                    count,
                    if (first_msg_id != 0L) 1 else 0,
                    idLastMessage,
                    callback
                )
            }
            Messengers.TELEGRAM -> {
                tg.getMessagesFromChat(chat.chatId, count, 0, first_msg_id, callback)
            }
        }
    }

    fun markAsRead(chat: Chat, message_ids: List<Long>, callback: (Int) -> Unit) {
        when(chat.messenger) {
            Messengers.VK -> {
                vk.markAsRead(
                    chat.chatId,
                    message_ids,
                    null,
                    false,
                    callback
                )
            }
            Messengers.TELEGRAM -> {
                tg.markAsRead(
                    chat.chatId,
                    message_ids,
                    null,
                    false,
                    callback
                )
            }
        }
    }

    fun startUpdateListener(chat: Chat, callback: (Event) -> Unit) {
        when(chat.messenger) {
            Messengers.VK -> {
                vk.startUpdateListener(callback)
            }
            Messengers.TELEGRAM -> {
                tg.startUpdateListener(callback)
            }
        }
    }

}