package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VKLongPoolApiService {
    @GET("{server}?act=a_check")
    fun getUpdates(
        @Path("server") server: String,
        @Query("key") key: String,
        @Query("ts") ts: Int,
        @Query("wait") wait: Int,
        @Query("mode") mode: Int,
        @Query("version") version: Int
    ): Call<JsonElement>
}