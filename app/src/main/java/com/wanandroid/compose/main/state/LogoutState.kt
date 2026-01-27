package com.wanandroid.compose.main.state


/**
 * Created by wenjie on 2026/01/27.
 */
sealed interface LogoutState {
    data object Nothing : LogoutState
    data object Loading : LogoutState
    data object Success : LogoutState
    data class Failure(val errorMsg: String) : LogoutState
}
