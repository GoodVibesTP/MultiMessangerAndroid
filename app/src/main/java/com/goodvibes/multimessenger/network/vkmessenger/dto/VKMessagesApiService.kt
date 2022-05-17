package com.goodvibes.multimessenger.network.vkmessenger.dto

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VKMessagesApiService {
    @GET("method/messages.getConversations?v=5.131")
    fun getConversations(
        @Query("access_token") access_token: String,
        @Query("count") count: Int = 200,
        @Query("offset") offset: Int = 0,
        @Query("extended") extended: Int = 1
    ): Call<VKRespond<VKMessagesGetConversationsResponse>>

    @GET("method/messages.getHistory?v=5.131")
    fun getHistory(
        @Query("access_token") access_token: String,
        @Query("peer_id") peer_id: Long,
        @Query("count") count: Int,
        @Query("offset") offset: Int = 0,
        @Query("random_id") random_id: Int = 0
    ): Call<VKRespond<VKMessagesGetHistoryResponse>>

    @GET("method/messages.getConversationsById?v=5.131")
    fun getConversationsById(
        @Query("access_token") access_token: String,
        @Query("peer_ids") peer_ids: Long,
        @Query("extended") extended: Int = 1
    ): Call<VKRespond<VKMessagesGetConversationsByIdResponse>>

    @GET("method/messages.send?v=5.131")
    fun send(
        @Query("access_token") access_token: String,
        @Query("user_id") user_id: Long,
        @Query("message") message: String,
        @Query("random_id") random_id: Int = 0
    ): Call<VKRespond<Long>>

    @GET("method/messages.markAsRead?v=5.131")
    fun markAsRead(
        @Query("access_token") access_token: String,
        @Query("peer_id") peer_id: Long,
        @Query("message_ids") message_ids: String?,
        @Query("start_message_id") start_message_id: Long?,
        @Query("mark_conversation_as_read") mark_conversation_as_read: Int = 0
    ): Call<VKRespond<Int>>

    @GET("method/messages.getLongPollServer?v=5.131")
    fun getLongPollServer(
        @Query("access_token") access_token: String,
        @Query("need_pts") need_pts: Int,
        @Query("lp_version") lp_version: Int
    ): Call<VKRespond<VKMessagesGetLongPoolServerResponse>>
}
