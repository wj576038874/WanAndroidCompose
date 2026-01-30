package com.wanandroid.compose.login

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
    suspend fun getUserCoinInfo() = loginApi.getUserInfo()

     /**
      * 退出登录
      */
     suspend fun logout() = loginApi.logout()

}