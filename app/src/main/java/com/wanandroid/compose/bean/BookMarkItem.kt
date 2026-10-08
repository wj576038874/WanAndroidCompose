package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

data class BookMarkItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("link") val link: String,
)
