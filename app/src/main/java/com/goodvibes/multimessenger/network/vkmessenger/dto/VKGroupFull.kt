package com.goodvibes.multimessenger.network.vkmessenger.dto

import com.google.gson.annotations.SerializedName

data class VKGroupFull(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("photo_50")
    val photo50: String? = null,
    @SerializedName("photo_100")
    val photo100: String? = null,
    @SerializedName("photo_200")
    val photo200: String? = null,
    @SerializedName("photo_400")
    val photo400: String? = null,
    @SerializedName("photo_max_orig")
    val photoMaxOrig: String? = null,
//    @SerializedName("screen_name")
//    val screenName: String? = null,
//    @SerializedName("fixed_post")
//    val fixedPost: Int? = null,
//    @SerializedName("has_photo")
//    val hasPhoto: BaseBoolInt? = null,
//    @SerializedName("crop_photo")
//    val cropPhoto: BaseCropPhoto? = null,
//    @SerializedName("status")
//    val status: String? = null,
//    @SerializedName("is_closed")
//    val isClosed: GroupsGroupIsClosed? = null,
//    @SerializedName("type")
//    val type: GroupsGroupType? = null,
//    @SerializedName("deactivated")
//    val deactivated: String? = null,
//    @SerializedName("photo_200_orig")
//    val photo200Orig: String? = null,
//    @SerializedName("photo_400")
//    val photo400: String? = null,
//    @SerializedName("photo_400_orig")
//    val photo400Orig: String? = null,
//    @SerializedName("photo_max")
//    val photoMax: String? = null,
//    @SerializedName("photo_max_orig")
//    val photoMaxOrig: String? = null,
)
