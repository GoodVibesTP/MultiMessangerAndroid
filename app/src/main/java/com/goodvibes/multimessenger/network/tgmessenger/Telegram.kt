package com.goodvibes.multimessenger.network.tgmessenger

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
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock


object Telegram : Messenger {
    override val messenger = Messengers.TELEGRAM

    private lateinit var activity: AppCompatActivity

    fun initClientWithActivity(activity: AppCompatActivity) {
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

    private fun toDefaultChat(chat: TdApi.Chat): Chat {
        return Chat(
            chatId = chat.id,
            img = R.drawable.kotik,
            imgUri = null,
            title = chat.title,
            chatType = ChatType.CHAT,
            lastMessage =
                if (chat.lastMessage == null) null
                else toDefaultMessage(chat.lastMessage!!),
            messenger = Messengers.TELEGRAM
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
            fwdMessages = null,
            replyTo = null,
            messenger = Messengers.TELEGRAM
        )
    }

    private var registeredForUpdates = false
    private var onEventsCallback: (Event) -> Unit = { }

    lateinit var client: Client

    val contacts = mutableMapOf<Long,TdApi.User>()
    val chats = mutableMapOf<Long, TdApi.Chat>()

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

    override fun isAuthorized(): Boolean {
        return haveAuthorization
    }

    override fun getAllChats(count: Int, first_msg: Int, callback: (MutableList<Chat>) -> Unit) {
        while(!haveAuthorization) {
        }
        client.send(
            TdApi.GetChats(null, count),
            CallbackHandler(callback)
        )
    }

    override fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        first_msg: Int,
        callback: (MutableList<Message>) -> Unit
    ) {
        GlobalScope.launch {
            delay(10000)
            Log.d(LOG_TAG, "getMessagesFromChat")
            client.send(
                TdApi.GetChatHistory(
                    chat_id,
                    10,
                    0,
                    100,
                    false
                ),
                CallbackHandler(callback)
            )
            delay(10000)
            client.send(
                TdApi.GetChatHistory(
                    chat_id,
                    10,
                    0,
                    100,
                    true
                ),
                CallbackHandler(callback)
            )
        }
    }

    override fun sendMessage(
        user_id: Long,
        text: String,
        callback: (Long) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getChatById(
        chat_id: Long,
        callback: (Chat) -> Unit
    ) {
        GlobalScope.launch {
            delay(2000)
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

    fun sendAuthCode(code: String) {
        client.send(
            TdApi.CheckAuthenticationCode(code),
            AuthorizationRequestHandler()
        )
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
//                val phoneNumber: String = "+79777569732"
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

    class CallbackHandler<T>(
        val callback: (T) -> Unit
    ) : Client.ResultHandler {
        override fun onResult(tdObject: TdApi.Object) {
            when(tdObject.constructor) {
                TdApi.Chats.CONSTRUCTOR -> {
                    val chatIds = (tdObject as TdApi.Chats).chatIds
                    val chatArray = arrayListOf<Chat>()
                    chatArray.ensureCapacity(chatIds.size)
                    for (chatId in chatIds) {
                        val telegramNextChat = chats[chatId]
                        if (telegramNextChat != null) {
                            chatArray.add(toDefaultChat(telegramNextChat))
                        }
                    }
                    callback(chatArray as T)
                }
                TdApi.Messages.CONSTRUCTOR -> {
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
    class UpdateHandler : Client.ResultHandler {
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

//                    for (chat in chats) {
//                        Log.d(LOG_TAG, chat.toString())
//                    }
                }
                TdApi.UpdateChatPosition.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatPosition")
                }
                TdApi.UpdateChatReadInbox.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatReadInbox")
                }
                TdApi.UpdateChatReadOutbox.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatReadOutbox")
                }
                TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateChatUnreadMentionCount")
                }
                TdApi.UpdateMessageEdited.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageEdited")
                }
                TdApi.UpdateMessageContent.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateMessageContent")
                }
                TdApi.UpdateNewMessage.CONSTRUCTOR -> {
                    Log.d(LOG_TAG, "UpdateHandler -> UpdateNewMessage, $tdObject")
                    val updateNewMessage = tdObject as TdApi.UpdateNewMessage
                    if (registeredForUpdates) {
                        onEventsCallback(Event.NewMessage(
                            message = toDefaultMessage(updateNewMessage.message),
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
