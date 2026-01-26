package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/26.
 */
data class UserInfo(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val icon: String,
    val type: Int,
    val collectIds: List<Int>,
    val coinCount: Int,
)
