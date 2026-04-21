package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/04/20.
 */
data class MessageItem(
    @SerializedName("category") val category: String? = null,
    @SerializedName("date") val date: Long = 0L,
    @SerializedName("fromUser") val fromUser: String? = null,
    @SerializedName("fromUserId") val fromUserId: Int = 0,
    @SerializedName("fullLink") val fullLink: String? = null,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("isRead") val isRead: Int = 0,
    @SerializedName("link") val link: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("niceDate") val niceDate: String? = null,
    @SerializedName("tag") val tag: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("userId") val userId: Int = 0,
)
