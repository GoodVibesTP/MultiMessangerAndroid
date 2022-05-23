package com.goodvibes.multimessenger.network.tgmessenger

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.goodvibes.multimessenger.R
import com.goodvibes.multimessenger.datastructure.*
import com.goodvibes.multimessenger.network.Messenger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.min


object Telegram : Messenger {
    override val messenger = Messengers.TELEGRAM

    private lateinit var activity: AppCompatActivity
    private var currentUserId: Long = 0L

    fun init(activity: AppCompatActivity) {
        this.activity = activity
        client = Client.create(
            UpdateHandler(),
            null,
            null
        )
    }

    private const val LOG_TAG = "MultiMessenger_TG_logs"

    @Volatile
    private var haveAuthorization = false
    @Volatile
    private var needQuit = false
    @Volatile
    private var canQuit = false

    private val defaultHandler: Client.ResultHandler = DefaultHandler()

    private val authorizationLock: Lock = ReentrantLock()
    private val gotAuthorization: Condition = authorizationLock.newCondition()

    private val libraryLoaded = try {
        System.loadLibrary("tdjni")
    } catch (e: UnsatisfiedLinkError) {
        e.printStackTrace()
    }

    private var registeredForUpdates = false
    private var onEventsCallback: (Event) -> Unit = { }

    private lateinit var client: Client

    private val contacts = mutableMapOf<Long,TdApi.User>()
    private val chats = mutableMapOf<Long, TdApi.Chat>()
    private var chatsLoaded = false

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("ru", "ru"))

    private fun toDefaultChat(chat: TdApi.Chat): Chat {
        val lastMessage = if(chat.lastMessage == null) null else {
            toDefaultMessage(chat.lastMessage!!)
        }
        if (lastMessage != null) {
            lastMessage.read = if (lastMessage.isMyMessage) {
                lastMessage.id <= chat.lastReadOutboxMessageId
            }
            else {
                lastMessage.id <= chat.lastReadInboxMessageId
            }
        }
        chat.photo
        return Chat(
            chatId = chat.id,
            img = R.mipmap.tg_icon,
            imgUri = null,
            title = chat.title,
            chatType = ChatType.CHAT,
            lastMessage = lastMessage,
            inRead = chat.lastReadInboxMessageId,
            outRead = chat.lastReadOutboxMessageId,
            messenger = Messengers.TELEGRAM,
            unreadMessage = chat.unreadCount,
        )
    }

    private fun toDefaultMessage(message: TdApi.Message): Message {
        return Message(
            id = message.id,
            chatId = message.chatId,
            userId = when(message.senderId.constructor) {
                TdApi.MessageSenderUser.CONSTRUCTOR -> {
                    (message.senderId as TdApi.MessageSenderUser).userId
                }
                TdApi.MessageSenderChat.CONSTRUCTOR -> {
                    (message.senderId as TdApi.MessageSenderChat).chatId
                }
                else -> {
                    0
                }
            },
            text = when(message.content.constructor) {
                TdApi.MessageText.CONSTRUCTOR -> {
                    (message.content as TdApi.MessageText).text.text
                }
                else -> {
                    "Текст сообщения не поддерживается данной версией приложения"
                }
            },
            date = message.date,
            time = dateFormat.format(message.date * 1000L),
            isMyMessage = when(message.senderId.constructor) {
                TdApi.MessageSenderUser.CONSTRUCTOR -> {
                    (message.senderId as TdApi.MessageSenderUser).userId == currentUserId
                }
                else -> false
            },
            fwdMessages = null,
            replyTo = null,
            messenger = Messengers.TELEGRAM,
            attachments = null
        )
    }

    fun sendAuthPhone(phone: String) {
        client.send(
            TdApi.SetAuthenticationPhoneNumber(
                phone,
                TdApi.PhoneNumberAuthenticationSettings(
                    false,
                    false,
                    false,
                    false,
                    null
                )
            ),
            AuthorizationRequestHandler()
        )
    }

    fun sendAuthCode(code: String) {
        client.send(
            TdApi.CheckAuthenticationCode(code),
            AuthorizationRequestHandler()
        )
    }

    fun logout() {
        client.send(
            TdApi.LogOut(),
            AuthorizationRequestHandler()
        )
    }

    override fun isAuthorized(): Boolean {
        return haveAuthorization
    }

    override fun getUserId(): Long {
        return currentUserId
    }
    fun downloadFile(file: TdApi.File) {
        client.send(
            TdApi.DownloadFile(
                file.id, 1,0,0,true
            ),
            AuthorizationRequestHandler()
        )
    }
    override fun getAllChats(count: Int, first_chat: Int, callback: (MutableList<Chat>) -> Unit) {
        Log.d("MM_LOG", "getAllChats")
        GlobalScope.launch {
            if (!haveAuthorization) {
                delay(1000)
            }
            if (haveAuthorization) {
                client.send(
                    TdApi.GetChats(null, count + first_chat)
                ) { tdObject ->
                    when (tdObject.constructor) {
                        TdApi.Chats.CONSTRUCTOR -> {
                            val chatIds = (tdObject as TdApi.Chats).chatIds
                            val chatArray = arrayListOf<Chat>()
                            val limit = min(first_chat + count, chatIds.size)
                            chatArray.ensureCapacity(limit)
                            for (i in first_chat until limit) {
                                val telegramNextChat = chats[chatIds[i]]
                                if (telegramNextChat != null) {
                                    chatArray.add(toDefaultChat(telegramNextChat))
                                }
                            }
                            callback(chatArray)
                        }
                        else -> Log.d(LOG_TAG, "Receive wrong response from TDLib: $tdObject")
                    }
                }
            }
        }
    }

    override fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        offset: Int,
        first_msg_id: Long,
        callback: (MutableList<Message>) -> Unit
    ) {
        Log.d("TG_LOG", "$offset $count")
        if (haveAuthorization) {
            Log.d("MM_LOG", "getMessagesFromChat")
            val messageList: MutableList<Message> = mutableListOf()

            getMessagesFromChat(
                chat_id,
                count,
                first_msg_id,
                messageList,
                callback
            )
        }
    }

    private fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        first_msg_id: Long,
        messageList: MutableList<Message>,
        callback: (MutableList<Message>) -> Unit
    ) {
        Log.d("MM_LOG", "getMessagesFromChat $count")
        client.send(
            TdApi.GetChatHistory(
                chat_id,
                first_msg_id,
                0,
                count,
                false
            ),
            CallbackHandler<MutableList<Message>> {
                messageList.addAll(it)

                if (count - it.size > 0 && it.size != 0) {
                    getMessagesFromChat(
                        chat_id,
                        count - it.size,
                        messageList.last().id,
                        messageList,
                        callback
                    )
                }
                else {
                    val chat = chats[chat_id]
                    for (message in messageList) {
                        message.read = if (message.isMyMessage) {
                            message.id <= chat!!.lastReadOutboxMessageId
                        }
                        else {
                            message.id <= chat!!.lastReadInboxMessageId
                        }
                    }
                    callback(messageList)
                }
            }
        )
    }

    override fun sendMessage(
        chat_id: Long,
        text: String,
        callback: (Long) -> Unit
    ) {
        client.send(
            TdApi.SendMessage(
                chat_id,
                0,
                0,
                null,
                null,
                TdApi.InputMessageText(
                    TdApi.FormattedText(text, null),
                    false,
                    false
                )
            ),
            SendMessageResultHandler(callback)
        )
    }

    override fun editMessage(
        chat_id: Long,
        message_id: Long,
        text: String,
        callback: (Long) -> Unit
    ) {
        client.send(
            TdApi.EditMessageText(
                chat_id,
                message_id,
                null,
                TdApi.InputMessageText(
                    TdApi.FormattedText(text, null),
                    false,
                    false
                )
            ),
            SendMessageResultHandler(callback)
        )
    }

    override fun deleteMessages(
        chat_id: Long,
        message_ids: List<Long>,
        callback: (List<Int>) -> Unit
    ) {
        client.send(
            TdApi.DeleteMessages(
                chat_id,
                message_ids.toLongArray(),
                false
            ),
            DefaultHandler()
        )
    }

    override fun markAsRead(
        peer_id: Long,
        message_ids: List<Long>?,
        start_message_id: Long?,
        mark_conversation_as_read: Boolean,
        callback: (Int) -> Unit
    ) {
        client.send(
            TdApi.ViewMessages(
                peer_id,
                0,
                message_ids?.toLongArray(),
                true
            ),
            MarkAsReadResultHandler(callback)
        )
    }

    override fun getChatById(
        chat_id: Long,
        callback: (Chat) -> Unit
    ) {
        if (haveAuthorization) {
            Log.d(LOG_TAG, "getChatById, ${chats[chat_id]}")
            Log.d(LOG_TAG, chats.toString())
            if (chats[chat_id] != null) {
                callback(toDefaultChat(chats[chat_id]!!))
            }
        }
    }


    override fun startUpdateListener(callback: (Event) -> Unit) {
        if (registeredForUpdates) registeredForUpdates = false
        onEventsCallback = callback
        registeredForUpdates = true
    }

    private var authorizationState: TdApi.AuthorizationState? = null

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState?) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState
        }
        when (this.authorizationState?.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                Log.d(LOG_TAG,
                    "onAuthorizationStateUpdated -> AuthorizationStateWaitTdlibParameters")
                val parameters = TdApi.TdlibParameters()
                parameters.databaseDirectory = File(activity.filesDir.path, "tdlib").absolutePath
                parameters.useMessageDatabase = true
                parameters.useSecretChats = true
                parameters.apiId = 15051271
                parameters.apiHash = "1fc02c8419bf541c67987681c0e81b6a"
                parameters.systemLanguageCode = "en"
                parameters.deviceModel = "Android"
                parameters.applicationVersion = "1.0"
                parameters.enableStorageOptimizer = true
                client.send(
                    TdApi.SetTdlibParameters(parameters),
                    AuthorizationRequestHandler()
                )
            }
            TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> {
                Log.d(LOG_TAG,
                    "onAuthorizationStateUpdated -> AuthorizationStateWaitEncryptionKey")
                client.send(
                    TdApi.CheckDatabaseEncryptionKey(),
                    AuthorizationRequestHandler()
                )
            }
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                Log.d(LOG_TAG,
                    "onAuthorizationStateUpdated -> AuthorizationStateWaitPhoneNumber")
//                val phoneNumber: String = "+7your_phone_number"
//                client.send(
//                    TdApi.SetAuthenticationPhoneNumber(
//                        phoneNumber,
//                        TdApi.PhoneNumberAuthenticationSettings(
//                            false,
//                            false,
//                            false,
//                            false,
//                            null
//                        )
//                    ),
//                    AuthorizationRequestHandler()
//                )
            }
            TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR -> {
                Log.d(LOG_TAG,
                    "onAuthorizationStateUpdated -> AuthorizationStateWaitOtherDeviceConfirmation")
                val link = (this.authorizationState as TdApi.AuthorizationStateWaitOtherDeviceConfirmation).link
                Log.d(LOG_TAG,"Please confirm this login link on another device: $link")
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateWaitCode")
                val code: String = "Please enter authentication code: "
//                client.send(
//                    TdApi.CheckAuthenticationCode(code),
//                    AuthorizationRequestHandler()
//                )
            }
            TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateWaitRegistration")
//                val firstName: String = "Please enter your first name: "
//                val lastName: String = "Please enter your last name: "
//                client.send(
//                    TdApi.RegisterUser(firstName, lastName),
//                    AuthorizationRequestHandler()
//                )
            }
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
//                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateWaitPassword")
//                val password: String = "Please enter password: "
//                client.send(
//                    TdApi.CheckAuthenticationPassword(password),
//                    AuthorizationRequestHandler()
//                )
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateReady")
                haveAuthorization = true
                authorizationLock.lock()
                try {
                    gotAuthorization.signal()
                } finally {
                    authorizationLock.unlock()
                }
                client.send(
                    TdApi.GetMe(),
                    GetMeResultHandler {
                        currentUserId = it.id
                    }
                )
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateLoggingOut")
                haveAuthorization = false
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateClosing")
                haveAuthorization = false
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                Log.d(LOG_TAG, "onAuthorizationStateUpdated -> AuthorizationStateClosed")
                if (!needQuit) {
                    client = Client.create(
                        UpdateHandler(),
                        null,
                        null
                    ) // recreate client after previous has closed
                } else {
                    canQuit = true
                }
            }
            else -> Log.d(LOG_TAG, "onAuthorizationStateUpdated -> " +
                    "Unsupported authorization state: ${this.authorizationState}")
        }
    }

    private class DefaultHandler : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            print(tdObject.toString())
        }
    }

    private class CallbackHandler<T>(
        val callback: (T) -> Unit
    ) : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            when(tdObject.constructor) {
                TdApi.Chats.CONSTRUCTOR -> {
//                    val chatIds = (tdObject as TdApi.Chats).chatIds
//                    val chatArray = arrayListOf<Chat>()
//                    chatArray.ensureCapacity(chatIds.size)
//                    for (chatId in chatIds) {
//                        val telegramNextChat = chats[chatId]
//                        if (telegramNextChat != null) {
//                            chatArray.add(toDefaultChat(telegramNextChat))
//                        }
//                    }
//                    callback(chatArray as T)
                }
                TdApi.Messages.CONSTRUCTOR -> {
                    Log.d("MM_LOG", tdObject.toString())
                    val messages = (tdObject as TdApi.Messages).messages
                    val messageArray = arrayListOf<Message>()
                    messageArray.ensureCapacity(messages.size)
                    for (message in messages) {
                        messageArray.add(toDefaultMessage(message))
                    }
                    callback(messageArray as T)
                }
            }
        }
    }

    private class SendMessageResultHandler(
        val callback: (Long) -> Unit
    ) : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            val sentMessage = (tdObject as TdApi.Message)
            callback(toDefaultMessage(sentMessage).id)
        }
    }

    private class MarkAsReadResultHandler(
        val callback: (Int) -> Unit
    ) : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            when (tdObject.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    Log.d("MM_LOG", "Receive an error: $tdObject")
                }
                TdApi.Ok.CONSTRUCTOR -> {
                    Log.d("MM_LOG", "Receive OK: $tdObject")
                    callback(1)
                }
                else -> Log.d("MM_LOG", "Receive wrong response from TDLib: $tdObject")
            }
        }
    }

    private class GetMeResultHandler(
        val callback: (TdApi.User) -> Unit
    ) : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            callback((tdObject as TdApi.User))
        }
    }

    private class UpdateHandler : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            when (tdObject.constructor) {
                TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateAuthorizationState")
                    onAuthorizationStateUpdated((tdObject as TdApi.UpdateAuthorizationState).authorizationState)
                }
                TdApi.UpdateUser.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateUser")
                    val updateUser = tdObject as TdApi.UpdateUser
                    val user = updateUser.user
                    if (user.isContact) {
                        contacts[user.id] = user
                    }
                }
                TdApi.UpdateUserStatus.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateUserStatus")
                }
                TdApi.UpdateBasicGroup.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateBasicGroup")
                }
                TdApi.UpdateSupergroup.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateSupergroup")
                }
                TdApi.UpdateSecretChat.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateSecretChat")
                }
                TdApi.UpdateNewChat.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateNewChat")
                    val updateNewChat = tdObject as TdApi.UpdateNewChat
                    val chat = updateNewChat.chat
                    synchronized(chat) {
                        chats[chat.id] = chat
                    }
                }
                TdApi.UpdateChatTitle.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatTitle")

                    val updateChat = tdObject as TdApi.UpdateChatTitle
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.title = updateChat.title
                        }
                    }
                }
                TdApi.UpdateChatPhoto.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatPhoto")
                }
                TdApi.UpdateChatLastMessage.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatLastMessage")
                    val updateChat = tdObject as TdApi.UpdateChatLastMessage
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.lastMessage = updateChat.lastMessage
                        }
                    }
                }
                TdApi.UpdateChatPosition.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatPosition")
                }
                TdApi.UpdateChatReadInbox.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatReadInbox")
                    val updateChatReadInbox = tdObject as TdApi.UpdateChatReadInbox
                    onEventsCallback(Event.ReadIngoingUntil(
                        chat_id = updateChatReadInbox.chatId,
                        message_id = updateChatReadInbox.lastReadInboxMessageId,
                        messenger = Messengers.TELEGRAM
                        )
                    )
                }
                TdApi.UpdateChatReadOutbox.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatReadOutbox")
                    val updateChatReadOutbox = tdObject as TdApi.UpdateChatReadOutbox
                    onEventsCallback(Event.ReadIngoingUntil(
                        chat_id = updateChatReadOutbox.chatId,
                        message_id = updateChatReadOutbox.lastReadOutboxMessageId,
                        messenger = Messengers.TELEGRAM
                        )
                    )
                }
                TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatUnreadMentionCount")
                }
                TdApi.UpdateMessageEdited.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageEdited")
                }
                TdApi.UpdateMessageContent.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageContent")
                    val updateMessageContent = tdObject as TdApi.UpdateMessageContent
                    if (registeredForUpdates) {
                        onEventsCallback(Event.EditMessageContent(
                            chat_id = updateMessageContent.chatId,
                            message_id = updateMessageContent.messageId,
                            text = when(updateMessageContent.newContent.constructor) {
                                TdApi.MessageText.CONSTRUCTOR -> {
                                    (updateMessageContent.newContent as TdApi.MessageText).text.text
                                }
                                else -> {
                                    "Текст сообщения не поддерживается данной версией приложения"
                                }
                            },
                            messenger = Messengers.TELEGRAM
                            )
                        )
                    }
                }
                TdApi.UpdateNewMessage.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateNewMessage, $tdObject")
                    val updateNewMessage = tdObject as TdApi.UpdateNewMessage
                    if (registeredForUpdates) {
                        val newMessage = toDefaultMessage(updateNewMessage.message)
                        newMessage.read = false
                        onEventsCallback(Event.NewMessage(
                            message = newMessage,
                            direction = Event.NewMessage.Direction.INGOING
                            )
                        )
                    }
                }
                TdApi.UpdateMessageMentionRead.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageMentionRead")
                }
                TdApi.UpdateMessageSendFailed.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageSendFailed")
                }
                TdApi.UpdateDeleteMessages.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateDeleteMessages")
                    val updateDeleteMessages = tdObject as TdApi.UpdateDeleteMessages
                    for (message_id in updateDeleteMessages.messageIds) {
                        onEventsCallback(Event.DeleteMessage(
                            chat_id = updateDeleteMessages.chatId,
                            message_id = message_id,
                            messenger = Messengers.TELEGRAM
                            )
                        )
                    }
                }
                TdApi.UpdateChatReplyMarkup.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatReplyMarkup")
                }
                TdApi.UpdateChatDraftMessage.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatDraftMessage")
                }
                TdApi.UpdateChatNotificationSettings.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatNotificationSettings")
                }
                TdApi.UpdateFile.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateFile")
                }
                TdApi.UpdateUserFullInfo.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateUserFullInfo")
                }
                TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateBasicGroupFullInfo")
                }
                TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateSupergroupFullInfo")
                }
                TdApi.UpdateMessageSendSucceeded.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageSendSucceeded")
                }
                else -> {
                    Log.d(LOG_TAG, "UpdateHandler default, tdObject = $tdObject")
                }
            }
        }
    }

    private class AuthorizationRequestHandler : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            when (tdObject.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "Receive an error: $tdObject")
                    // onAuthorizationStateUpdated(null) // repeat last action
                }
                TdApi.Ok.CONSTRUCTOR -> {
                }
                else -> Log.d(LOG_TAG, "Receive wrong response from TDLib: $tdObject")
            }
        }
    }

    override fun authorize() {
        TODO("Not yet implemented")
    }
}
