package com.wanandroid.compose.share

import com.google.gson.annotations.SerializedName
import com.wanandroid.compose.bean.BasePageData

/**
 * Created by wenjie on 2026/04/21.
 */
data class ShareArticleData(
    @SerializedName("shareArticles") val shareArticles: BasePageData<ShareArticleItem>? = null,
)

data class ShareArticleItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("shareUser") val shareUser: String? = null,
    @SerializedName("niceDate") val niceDate: String? = null,
    @SerializedName("chapterName") val chapterName: String? = null,
    @SerializedName("superChapterName") val superChapterName: String? = null,
    @SerializedName("desc") val desc: String? = null,
    @SerializedName("envelopePic") val envelopePic: String? = null,
)
