package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/22.
 */
data class BannerItem(
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)
