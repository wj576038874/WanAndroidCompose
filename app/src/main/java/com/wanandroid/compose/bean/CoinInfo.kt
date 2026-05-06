package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/30.
 */
data class CoinInfo(
    @SerializedName("coinCount") val coinCount: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("rank") val rank: Int,
    @SerializedName("username") val username: String,
    @SerializedName("userId") val userId: Int,
)
