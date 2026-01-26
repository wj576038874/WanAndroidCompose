package com.wanandroid.compose.login

import com.wanandroid.compose.http.LoginApi

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginRepository(
    private val loginApi: LoginApi
) {

    suspend fun login(
        userName: String,
        password: String
    ) = loginApi.login(userName, password)
}