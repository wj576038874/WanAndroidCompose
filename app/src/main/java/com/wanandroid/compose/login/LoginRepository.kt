package com.wanandroid.compose.login

import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginRepository @Inject constructor(
    private val loginApi: LoginApi
) {

    /**
     * 登录
     */
    suspend fun login(
        userName: String, password: String
    ) = runCatching {
        val response = loginApi.login(userName, password)
        if (response.isSuccess) {
            response.data ?: throw Exception("data is null")
        } else {
            throw Exception(response.message)
        }
    }

    /**
     * 获取用户积分信息
     */
    suspend fun getUserCoinInfo() = runCatching {
        val response = loginApi.getUserInfo()
        if (response.isSuccess) {
            response.data ?: throw Exception("data is null")
        } else {
            throw Exception(response.message)
        }
    }

    /**
     * 退出登录
     */
    suspend fun logout() = runCatching {
        val response = loginApi.logout()
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message)
        }
    }

}