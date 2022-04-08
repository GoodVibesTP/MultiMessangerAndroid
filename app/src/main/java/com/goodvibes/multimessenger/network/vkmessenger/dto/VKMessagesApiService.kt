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
        @Query("peer_id") peer_id: Int,
        @Query("count") count: Int,
        @Query("offset") offset: Int = 0,
        @Query("random_id") random_id: Int = 0
    ): Call<VKRespond<VKMessagesGetHistoryResponse>>

    @GET("method/messages.send?v=5.131")
    fun send(
        @Query("access_token") access_token: String,
        @Query("user_id") user_id: Int,
        @Query("message") message: String,
        @Query("random_id") random_id: Int = 0
    ): Call<VKRespond<Int>>

    @GET("method/messages.getLongPollServer?v=5.131")
    fun getLongPollServer(
        @Query("access_token") access_token: String,
        @Query("need_pts") need_pts: Int,
        @Query("lp_version") lp_version: Int
    ): Call<VKRespond<VKMessagesGetLongPoolServerResponse>>
}
