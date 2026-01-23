package com.wanandroid.compose.bean

import kotlinx.serialization.Serializable

/**
 * Created by wenjie on 2026/01/22.
 */
@Serializable
data class ArticleItem(
    val id: Int,
    val title: String,
    val link: String,
    val niceDate: String,
    val author: String,
    val shareUser: String,
    val shareDate: Long,
    val niceShareDate: String,
    val superChapterName: String,
    val chapterName: String,
    val collect: Boolean,
)