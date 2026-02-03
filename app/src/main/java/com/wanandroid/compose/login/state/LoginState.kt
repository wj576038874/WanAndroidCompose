package com.wanandroid.compose.login.state

import com.wanandroid.compose.bean.UserInfo

/**
 * Created by wenjie on 2026/01/23.
 */
data class LoginState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null
)
