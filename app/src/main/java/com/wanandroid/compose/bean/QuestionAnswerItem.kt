package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/23.
 */
data class QuestionAnswerItem(
    val id: Int,
    val desc: String? = null,
    val author: String? = null,
    val link: String? = null,
    val title: String? = null,
    val time: String? = null,
    val superChapterName: String? = null,
    val chapterName: String? = null,
    val collect: Boolean,
    val niceDate: String? = null,
)