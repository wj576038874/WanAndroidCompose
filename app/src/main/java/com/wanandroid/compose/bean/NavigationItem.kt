package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/23.
 */
data class NavigationItem(
    @SerializedName("cid") val cid: Int,
    @SerializedName("name") val name: String,
    @SerializedName("articles") val articles: List<ArticleItem>
)
