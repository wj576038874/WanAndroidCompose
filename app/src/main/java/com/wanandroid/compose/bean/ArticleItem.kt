package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Created by wenjie on 2026/01/22.
 */
@Serializable
data class ArticleItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("niceDate") val niceDate: String,
    @SerializedName("author") val author: String,
    @SerializedName("shareUser") val shareUser: String,
    @SerializedName("shareDate") val shareDate: Long,
    @SerializedName("desc") val desc: String,
    @SerializedName("originId") val originId: Int,
    @SerializedName("niceShareDate") val niceShareDate: String,
    @SerializedName("superChapterName") val superChapterName: String,
    @SerializedName("chapterName") val chapterName: String,
    @SerializedName("collect") val collect: Boolean,
    @SerializedName("envelopePic") val envelopePic: String? = null
)
