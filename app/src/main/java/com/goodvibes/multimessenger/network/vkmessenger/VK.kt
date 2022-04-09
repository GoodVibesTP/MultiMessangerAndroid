package com.goodvibes.multimessenger.network.vkmessenger

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.goodvibes.multimessenger.datastructure.Chat
import com.goodvibes.multimessenger.datastructure.Message
import com.goodvibes.multimessenger.datastructure.Messengers
import com.goodvibes.multimessenger.network.Messenger
import com.goodvibes.multimessenger.network.vkmessenger.dto.*
import com.google.gson.JsonElement
import com.vk.api.sdk.VK as OriginalVKClient
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class VK(
    private val activity: AppCompatActivity
) : Messenger {
    private val LOG_TAG = "VK_LOG"

    override val messenger = Messengers.VK
    private val vkClient = OriginalVKClient
    private var token = ""

    private val permissions = arrayListOf<VKScope>()

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
        }
    }

    private val authLauncher = vkClient.login(activity) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                // User passed authorization
                Log.d(LOG_TAG,"User passed authorization, token=${result.token.accessToken}")
            }
            is VKAuthenticationResult.Failed -> {
                // User didn't pass authorization
                Log.d(LOG_TAG,
                    "User didn't pass authorization, exception = ${result.exception}")
            }
        }
    }

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.vk.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val messagesService = retrofit.create(VKMessagesApiService::class.java)
        private val usersService = retrofit.create(VKUsersApiService::class.java)

        private var longPoolService: VKLongPoolApiService? = null
    }

    override fun getAllChats(
        count: Int,
        first_msg: Int,
        callback: (List<Chat>) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"

        val callForVKRespond: Call<VKRespond<VKMessagesGetConversationsResponse>>
            = messagesService.getConversations(
            access_token = this.token,
            count = count,
            offset = first_msg
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
                                val nextChat = toDefaultChat(item, responseBody.response)
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
        chat_id: Int,
        count: Int,
        first_msg: Int,
        callback: (List<Message>) -> Unit
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
                                val nextMessage = toDefaultMessage(item)
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

    override fun sendMessage(
        user_id: Int,
        text: String,
        callback: (Int) -> Unit
    ) {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<Int>> = messagesService.send(
            access_token = this.token,
            user_id = user_id,
            message = text
        )

        callForVKRespond.enqueue(object : Callback<VKRespond<Int>> {
            override fun onResponse(
                call: Call<VKRespond<Int>>,
                response: Response<VKRespond<Int>>
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
                call: Call<VKRespond<Int>>,
                t: Throwable
            ) {
                Log.d(LOG_TAG, "$methodName failure: $t")
            }
        })

        Log.d(LOG_TAG, "$methodName request: ${callForVKRespond.request()}")
    }

    fun startUpdateListener() {
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
                            longPoolService = Retrofit.Builder()
                                .baseUrl("https://${serverUrl[0]}/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                                .create(VKLongPoolApiService::class.java)

                            val callForUpdates: Call<JsonElement> = longPoolService!!.getUpdates(
                                server = serverUrl[1],
                                key = responseBody.response.key,
                                ts = responseBody.response.ts,
                                wait = 1,
                                mode = 2,
                                version = 3
                            )

                            callForUpdates.enqueue(longPoolResponse)
                            Log.d(LOG_TAG, "$methodName request: ${callForUpdates.request()}")

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

    fun getUserInfo(user_ids: String, fields: String = "") {
        val methodName = "${this.javaClass.name}->${object {}.javaClass.enclosingMethod?.name}"
        val callForVKRespond: Call<VKRespond<List<VKUserFull>>> = usersService.get(
            access_token = this.token,
            user_ids = user_ids,
            fields = fields
        )

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
        authLauncher.launch(permissions)
    }
}

object longPoolResponse : Callback<JsonElement> {
    private const val methodName = "longPoolResponse"
    override fun onResponse(
        call: Call<JsonElement>,
        response: Response<JsonElement>
    ) {
        val LOG_TAG = "VK_LOG"

        Log.d(LOG_TAG, "$methodName response code: ${response.code()}")
        if (response.isSuccessful) {
            val responseBody = response.body()
            when {
                responseBody == null -> {
                    Log.d(LOG_TAG, "$methodName successful, but response.body() is null")
                }
                else -> {
                    Log.d(
                        LOG_TAG,
                        responseBody.asString
                    )
                }
            }
        }
    }

    override fun onFailure(
        call: Call<JsonElement>,
        t: Throwable
    ) {
        val LOG_TAG = "VK_LOG"
        Log.d(LOG_TAG, "$methodName failure: $t")
    }
}
