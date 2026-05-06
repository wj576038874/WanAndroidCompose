package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/26.
 */
data class UserInfoData(
    @SerializedName("userInfo") val userInfo: UserInfo? = null,
    @SerializedName("coinInfo") val coinInfo: CoinInfo? = null,
)
