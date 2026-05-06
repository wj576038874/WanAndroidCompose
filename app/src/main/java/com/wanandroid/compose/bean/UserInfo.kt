package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/26.
 */
data class UserInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("type") val type: Int,
    @SerializedName("collectIds") val collectIds: Set<Int>,
    @SerializedName("coinCount") val coinCount: Int,
)
