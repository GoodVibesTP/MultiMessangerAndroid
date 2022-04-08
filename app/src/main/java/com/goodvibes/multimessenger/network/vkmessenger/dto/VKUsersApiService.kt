package com.goodvibes.multimessenger.network.vkmessenger.dto

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VKUsersApiService {
    @GET("method/users.get?v=5.131")
    fun get(
        @Query("access_token") access_token: String,
        @Query("user_ids") user_ids: String,
        @Query("fields") fields: String
    ): Call<VKRespond<List<VKUserFull>>>
}
