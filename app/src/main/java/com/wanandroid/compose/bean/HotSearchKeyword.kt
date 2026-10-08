package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

data class HotSearchKeyword(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
)
