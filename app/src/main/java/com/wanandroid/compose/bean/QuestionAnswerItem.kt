package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/23.
 */
data class QuestionAnswerItem(
    @SerializedName("id") val id: Int,
    @SerializedName("desc") val desc: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("superChapterName") val superChapterName: String? = null,
    @SerializedName("chapterName") val chapterName: String? = null,
    @SerializedName("collect") val collect: Boolean,
    @SerializedName("niceDate") val niceDate: String? = null,
    @SerializedName("tags") val tags: List<Tag>? = null
)

data class Tag(
    @SerializedName("name") val name: String? = null,
    @SerializedName("url") val url: String? = null
)
