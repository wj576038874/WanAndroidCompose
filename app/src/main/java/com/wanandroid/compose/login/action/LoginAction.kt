package com.wanandroid.compose.login.action

/**
 * Created by wenjie on 2026/02/06.
 */
sealed class LoginAction {
    data class InputUserName(val userName: String) : LoginAction()
    data class InputPassword(val password: String) : LoginAction()
    data class UpdateIsPasswordVisible(val isPasswordVisible: Boolean) : LoginAction()
    object Login : LoginAction()
}