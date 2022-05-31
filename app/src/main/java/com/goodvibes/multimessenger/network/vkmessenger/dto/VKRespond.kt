package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKRespond<T>(
    @SerializedName("response")
    val response: T?,
    @SerializedName("error")
    val error: VKErrorResponse?
)
