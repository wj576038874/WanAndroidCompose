package com.wanandroid.compose.login.state

import com.wanandroid.compose.bean.UserInfo

/**
 * Created by wenjie on 2026/01/23.
 */
sealed interface LoginState{
    data object Nothing : LoginState
    data object Loading : LoginState
    data class Success(val userInfo: UserInfo) : LoginState
    data class Failure(val errorMsg: String) : LoginState
}
