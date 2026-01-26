package com.wanandroid.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.main.state.LoginResult
import com.wanandroid.compose.main.state.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/26.
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(
        userName: String,
        password: String
    ) {
        viewModelScope.launch {
            runCatching {
                _loginState.value = _loginState.value.copy(loginResult = LoginResult.Loading)
                delay(1000)
                loginRepository.login(userName, password)
            }.onSuccess {
                it.data?.let { userInfo ->
                    _loginState.value = _loginState.value.copy(
                        loginResult = LoginResult.Success(userInfo)
                    )
                } ?: run {
                    _loginState.value = _loginState.value.copy(
                        loginResult = LoginResult.Failure("登录失败"),
                    )
                }
            }.onFailure {
                _loginState.value = _loginState.value.copy(
                    loginResult = LoginResult.Failure(it.message ?: "登录失败")
                )
            }
        }
    }

}