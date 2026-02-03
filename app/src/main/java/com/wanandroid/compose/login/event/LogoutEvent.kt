package com.wanandroid.compose.login.event

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class LogoutEvent {
    object LogoutSuccess : LogoutEvent()
    class LogoutFailure(val errorMsg: String) : LogoutEvent()
}