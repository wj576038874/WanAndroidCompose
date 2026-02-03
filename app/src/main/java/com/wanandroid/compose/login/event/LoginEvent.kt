package com.wanandroid.compose.login.event

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
    class LoginFailure(val errorMsg: String) : LoginEvent()
}