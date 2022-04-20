package com.goodvibes.multimessenger.network.vkmessenger

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Event
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.Messenger
import com.goodvibes.multimessenger.network.vkmessenger.dto.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.vk.api.sdk.VK as OriginalVKClient
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


object VK : Messenger {
    override val messenger = Messengers.VK

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale("ru", "ru"))

    private lateinit var activity: AppCompatActivity

    fun init(activity: AppCompatActivity) {
        this.activity = activity
        getUserInfo(null) {
            currentUserId = it[0].id
        }
    }

    private var haveAuthorization = true
    private var currentUserId = 0L

    private val vkClient = OriginalVKClient

    private var token = "9344d5de74ceb93309d46178a21cf650cc0645cf733064fa46ba65bb19a9f31f973884e139a28569d5647"


    private val permissions = arrayListOf<VKScope>()

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
        }
    }

//    private val authLauncher = vkClient.login(activity) { result : VKAuthenticationResult ->
//        when (result) {
//            is VKAuthenticationResult.Success -> {
//                // User passed authorization
//                Log.d(LOG_TAG,"User passed authorization, token=${result.token.accessToken}")
//            }
//            is VKAuthenticationResult.Failed -> {
//                // User didn't pass authorization
//                Log.d(LOG_TAG,
//                    "User didn't pass authorization, exception = ${result.exception}")
//            }
//        }
//    }

    private const val LOG_TAG = "MultiMessenger_VK_logs"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.vk.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val messagesService = retrofit.create(VKMessagesApiService::class.java)
    private val usersService = retrofit.create(VKUsersApiService::class.java)

    override fun isAuthorized(): Boolean {
        return haveAuthorization
    }

    override fun getUserId(): Long {
        return currentUserId
    }

    override fun getAllChats(
        count: Int,
        first_chat: Int,
        callback: (MutableList<Chat>) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"

        val callForVKRespond: Call<VKRespond<VKMessagesGetConversationsResponse>>
            = messagesService.getConversations(
            access_token = this.token,
            count = count,
            offset = first_chat
        )

        callForVKRespond.enqueue(object : Callback<VKRespond<VKMessagesGetConversationsResponse>> {
            override fun onResponse(
                call: Call<VKRespond<VKMessagesGetConversationsResponse>>,
                response: Response<VKRespond<VKMessagesGetConversationsResponse>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )
                            Log.d(
                                LOG_TAG,
                                "$methodName: get ${responseBody.response.count} chats"
                            )
                            val chatArray = arrayListOf<Chat>()
                            chatArray.ensureCapacity(responseBody.response.count)
                            for (item in responseBody.response.items) {
                                val nextChat = toDefaultChat(item, responseBody.response, currentUserId)
                                chatArray.add(nextChat)
                            }
                            callback(chatArray)
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<VKMessagesGetConversationsResponse>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    override fun getMessagesFromChat(
        chat_id: Long,
        count: Int,
        first_msg: Int,
        callback: (MutableList<Message>) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<VKMessagesGetHistoryResponse>> = messagesService.getHistory(
            access_token = this.token,
            peer_id = chat_id,
            count = count,
            offset = first_msg
        )

        callForVKRespond.enqueue(object : Callback<VKRespond<VKMessagesGetHistoryResponse>> {
            override fun onResponse(
                call: Call<VKRespond<VKMessagesGetHistoryResponse>>,
                response: Response<VKRespond<VKMessagesGetHistoryResponse>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )
                            val messagesArray = arrayListOf<Message>()
                            messagesArray.ensureCapacity(responseBody.response.count)
                            for (item in responseBody.response.items) {
                                val nextMessage = toDefaultMessage(item, currentUserId)
                                if (nextMessage != null) {
                                    messagesArray.add(nextMessage)
                                }
                            }
                            callback(messagesArray)
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<VKMessagesGetHistoryResponse>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    override fun getChatById(
        chat_id: Long,
        callback: (Chat) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<VKMessagesGetConversationsByIdResponse>> = messagesService.getConversationsById(
            access_token = this.token,
            peer_ids = chat_id
        )

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")

        callForVKRespond.enqueue(object : Callback<VKRespond<VKMessagesGetConversationsByIdResponse>> {
            override fun onResponse(
                call: Call<VKRespond<VKMessagesGetConversationsByIdResponse>>,
                response: Response<VKRespond<VKMessagesGetConversationsByIdResponse>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )
                            Log.d(
                                LOG_TAG,
                                "$methodName: get ${responseBody.response.count} chats"
                            )
                            val conversationWithMessage = VKMessagesConversationWithMessage(
                                conversation = responseBody.response.items[0],
                                lastMessage = null
                            )
                            val newChat = toDefaultChat(conversationWithMessage, responseBody.response, currentUserId)
                            callback(newChat)
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<VKMessagesGetConversationsByIdResponse>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })
    }

    override fun sendMessage(
        user_id: Long,
        text: String,
        callback: (Long) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<Long>> = messagesService.send(
            access_token = this.token,
            user_id = user_id,
            message = text
        )

        callForVKRespond.enqueue(object : Callback<VKRespond<Long>> {
            override fun onResponse(
                call: Call<VKRespond<Long>>,
                response: Response<VKRespond<Long>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )
                            callback(responseBody.response)
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<Long>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    override fun startUpdateListener(callback: (Event) -> Unit) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<VKMessagesGetLongPoolServerResponse>> =
            messagesService.getLongPollServer(
            access_token = this.token,
            need_pts = 1,
            lp_version = 3
        )

        callForVKRespond.enqueue(object : Callback<VKRespond<VKMessagesGetLongPoolServerResponse>> {
            override fun onResponse(
                call: Call<VKRespond<VKMessagesGetLongPoolServerResponse>>,
                response: Response<VKRespond<VKMessagesGetLongPoolServerResponse>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )

                            val serverUrl = responseBody.response.server.split("/")

                            val longPoolService = Retrofit.Builder()
                                .baseUrl("https://${serverUrl[0]}/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(VKLongPoolApiService::class.java)

                            listenForEvents(
                                longPoolApiService = longPoolService,
                                server = serverUrl[1],
                                key = responseBody.response.key,
                                ts = responseBody.response.ts,
                                callback = callback
                            )
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<VKMessagesGetLongPoolServerResponse>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    private fun getUserInfo(
        user_ids: String?,
        fields: String = "",
        callback: (List<VKUserFull>) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<List<VKUserFull>>> = if (user_ids == null) {
            usersService.get(
                access_token = this.token,
                fields = fields
            )
        }
        else {
            usersService.get(
                access_token = this.token,
                user_ids = user_ids,
                fields = fields
            )
        }

        callForVKRespond.enqueue(object : Callback<VKRespond<List<VKUserFull>>> {
            override fun onResponse(
                call: Call<VKRespond<List<VKUserFull>>>,
                response: Response<VKRespond<List<VKUserFull>>>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    when {
                        responseBody == null -> {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                        }
                        responseBody.response != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName successful"
                            )
                            Log.d(LOG_TAG, "$methodName: get ${responseBody.response[0].firstName} " +
                                    "${responseBody.response[0].lastName} ${responseBody.response[0].sex}")
                            callback(responseBody.response)
                        }
                        responseBody.error != null -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName error ${responseBody.error.errorCode}: ${responseBody.error.errorMsg}"
                            )
                        }
                        else -> {
                            Log.d(
                                LOG_TAG,
                                "$methodName response is null && error is null"
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<VKRespond<List<VKUserFull>>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    override fun authorize() {
//        authLauncher.launch(permissions)
    }

    private fun listenForEvents(
        longPoolApiService: VKLongPoolApiService,
        server: String,
        key: String,
        ts: Int,
        callback: (Event) -> Unit
    ) {
        val methodName = "listenForEvents"
        val callForUpdates: Call<VKGetUpdates> = longPoolApiService.getUpdates(
            server = server,
            key = key,
            ts = ts,
            wait = 5,
            mode = 2,
            version = 3
        )

        val listenForEventsCallback = object : Callback<VKGetUpdates> {
            override fun onResponse(
                call: Call<VKGetUpdates>,
                response: Response<VKGetUpdates>
            ) {
                Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody == null) {
                            Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                    }
                    else {
                        Log.d(
                            LOG_TAG,
                            "ts: ${responseBody.ts}"
                        )
                        for (updateItem in responseBody.updates) {
                            val event: Event? = when(updateItem[0].asInt) {
                                VK_UPDATE.EVENTS.NEW_MESSAGE -> {
                                    if (updateItem.size > VK_UPDATE.NEW_MESSAGE.ADDITIONAL_FIELD) {
                                        val ADDITIONAL_FIELD = VK_UPDATE.NEW_MESSAGE.ADDITIONAL_FIELD
                                        if (updateItem[ADDITIONAL_FIELD].isJsonObject) {
                                            updateItem[ADDITIONAL_FIELD].asJsonObject.get("fwd")?.asString
                                        }
                                        else {
                                            Log.d(LOG_TAG, "$methodName field $ADDITIONAL_FIELD exists, " +
                                                    "but isJsonObject = false")
                                        }
                                    }
                                    val datetime = (System.currentTimeMillis() / 1000L).toInt()
                                    Event.NewMessage(
                                        message = Message(
                                            id = updateItem[VK_UPDATE.NEW_MESSAGE.MESSAGE_ID].asLong,
                                            chatId = updateItem[VK_UPDATE.NEW_MESSAGE.MINOR_ID].asLong,
                                            userId = updateItem[VK_UPDATE.NEW_MESSAGE.MINOR_ID].asLong,
                                            text = updateItem[VK_UPDATE.NEW_MESSAGE.TEXT].asString,
                                            isMyMessage = updateItem[VK_UPDATE.NEW_MESSAGE.FLAGS].asInt and 2 == 0,
                                            date = datetime,
                                            time = dateFormat.format(datetime * 1000L),
                                            fwdMessages = null,
                                            replyTo = null,
                                            messenger = Messengers.VK
                                        ),
                                        direction = if (updateItem[VK_UPDATE.NEW_MESSAGE.FLAGS].asInt and 2 == 0) {
                                            Event.NewMessage.Direction.INGOING
                                        }
                                        else {
                                            Event.NewMessage.Direction.OUTGOING
                                        }
                                    )
                                }
                                else -> {
                                    Log.d(
                                        LOG_TAG,
                                        "update_code: ${updateItem[0].asInt}"
                                    )
                                    null
                                }
                            }

                            if (event != null) {
                                callback(event)
                            }
                        }
                    }
                    listenForEvents(
                        longPoolApiService = longPoolApiService,
                        server = server,
                        key = key,
                        ts = responseBody?.ts ?: ts,
                        callback = callback
                    )
                }
            }

            override fun onFailure(
                call: Call<VKGetUpdates>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        }

        callForUpdates.enqueue(listenForEventsCallback)
        Log.d(LOG_TAG, "$methodName request: ${callForUpdates.request()}")
    }
}
