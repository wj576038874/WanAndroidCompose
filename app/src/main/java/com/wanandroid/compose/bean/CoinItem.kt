package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/27.
 */
data class CoinItem(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val reason: String,
    val type: Int,
    val userId: Int,
    val userName: String
)
