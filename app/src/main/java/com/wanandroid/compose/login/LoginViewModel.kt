package com.wanandroid.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.login.state.LoginState
import com.wanandroid.compose.login.state.LogoutState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Nothing)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Nothing)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private var loginJob: Job? = null
    private var logoutJob: Job? = null

    fun login(
        userName: String, password: String
    ) {
        loginJob = viewModelScope.launch {
            runCatching {
                _loginState.value = LoginState.Loading
                loginRepository.login(userName, password)
            }.onSuccess {
                it.data?.let { userInfo ->
                    _loginState.value = LoginState.Success(userInfo = userInfo)
                    UserManager.instance.login(userInfo)
                }
            }.onFailure {
                if (it is CancellationException) {
                    _loginState.value = LoginState.Failure(errorMsg = "取消登陆")
                } else {
                    _loginState.value = LoginState.Failure(errorMsg = it.message ?: "登录失败")
                }
            }
        }
    }

    fun cancelLogin() {
        _loginState.value = LoginState.Nothing
        loginJob?.cancel()
    }

    fun cancelLogout() {
        _logoutState.value = LogoutState.Nothing
        logoutJob?.cancel()
    }


    fun logout() {
        logoutJob = viewModelScope.launch {
            runCatching {
                _logoutState.value = LogoutState.Loading
                loginRepository.logout()
            }.onSuccess {
                if (it.isSuccess) {
                    UserManager.instance.logout()
                    _logoutState.value = LogoutState.Success
                } else {
                    _logoutState.value = LogoutState.Failure(errorMsg = it.message ?: "退出登录失败")
                }
            }.onFailure {
                _logoutState.value = LogoutState.Nothing
                _logoutState.value = LogoutState.Failure(errorMsg = it.message ?: "退出登录失败")
            }
        }
    }
}