package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

enum class VKSex(
    val value: Int
) {
    @SerializedName("0")
    UNKNOWN(0),

    @SerializedName("1")
    FEMALE(1),

    @SerializedName("2")
    MALE(2);
}
