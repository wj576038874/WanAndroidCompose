package com.wanandroid.compose.login

import com.wanandroid.compose.http.LoginApi

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginRepository(
    private val loginApi: LoginApi
) {

    /**
     * 登录
     */
    suspend fun login(
        userName: String,
        password: String
    ) = loginApi.login(userName, password)

    /**
     * 获取用户积分信息
     */
    suspend fun getUserCoinInfo() = loginApi.getUserCoinInfo()

     /**
      * 退出登录
      */
     suspend fun logout() = loginApi.logout()

}