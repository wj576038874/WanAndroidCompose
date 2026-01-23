package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/23.
 */
data class NavigationItem(
    val cid: Int,
    val name: String,
    val articles: List<ArticleItem>
)
