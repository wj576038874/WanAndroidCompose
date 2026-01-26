package com.wanandroid.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.main.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Nothing)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(
        userName: String,
        password: String
    ) {
        viewModelScope.launch {
            runCatching {
                _loginState.value = LoginState.Loading
                loginRepository.login(userName, password)
            }.onSuccess {
                it.data?.let { userInfo ->
                    _loginState.value = LoginState.Success(userInfo = userInfo)
                    UserManager.instance.login(userInfo)
                }
            }.onFailure {
                _loginState.value = LoginState.Failure(errorMsg = it.message ?: "登录失败")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                loginRepository.logout()
            }.onSuccess {
                if (it.isSuccess) {
                    UserManager.instance.logout()
                } else {
                    _loginState.value = LoginState.Failure(errorMsg = it.message ?: "退出登录失败")
                }
            }.onFailure {
                _loginState.value = LoginState.Failure(errorMsg = it.message ?: "退出登录失败")
            }
        }
    }
}