package com.wanandroid.compose.main.state

import com.wanandroid.compose.bean.UserInfo

/**
 * Created by wenjie on 2026/01/23.
 */
data class LoginState(
    val loginResult: LoginResult = LoginResult.Loading
)

sealed interface LoginResult {
    data object Loading : LoginResult
    data class Success(val userInfo: UserInfo) : LoginResult
    data class Failure(val errorMsg: String) : LoginResult
}
