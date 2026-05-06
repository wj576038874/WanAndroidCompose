package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/27.
 */
data class CoinItem(
    @SerializedName("coinCount") val coinCount: Int,
    @SerializedName("date") val date: Long,
    @SerializedName("desc") val desc: String,
    @SerializedName("id") val id: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("type") val type: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("userName") val userName: String
)
