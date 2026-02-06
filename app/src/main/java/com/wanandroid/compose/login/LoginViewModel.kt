package com.wanandroid.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.login.action.LoginAction
import com.wanandroid.compose.login.event.LoginEvent
import com.wanandroid.compose.login.event.LogoutEvent
import com.wanandroid.compose.login.state.LoginState
import com.wanandroid.compose.login.state.LogoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by wenjie on 2026/01/26.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _logoutState = MutableStateFlow(LogoutState())
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _loginEventChannel = Channel<LoginEvent>()
    val loginEvent = _loginEventChannel.receiveAsFlow()

    private val _logoutEventChannel = Channel<LogoutEvent>()
    val logoutEvent = _logoutEventChannel.receiveAsFlow()


    private var loginJob: Job? = null
    private var logoutJob: Job? = null


    init {
        _loginState.distinctUntilChangedBy { state ->
            state.userName
        }.map {
            it.userName.isNotBlank() && it.userName.length >= 6
        }.onEach { isUserNameValid ->
            _loginState.update { state ->
                state.copy(
                    isUserNameValid = isUserNameValid
                )
            }
        }.launchIn(viewModelScope)

        _loginState.distinctUntilChangedBy { state ->
            state.password
        }.map {
            it.password.isNotBlank() && it.password.length >= 6
        }.onEach { isPasswordValid ->
            _loginState.update { state ->
                state.copy(
                    isPasswordValid = isPasswordValid
                )
            }
        }.launchIn(viewModelScope)

        _loginState.onEach {
            _loginState.update { state ->
                it.copy(
                    canLogin = state.isPasswordValid && state.isUserNameValid
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun login() {
        loginJob = viewModelScope.launch {
            _loginState.update {
                it.copy(isLoading = true, userInfo = null)
            }
            delay(1000)
            loginRepository.login(_loginState.value.userName, _loginState.value.password).apply {
                onSuccess { userInfo ->
                    _loginState.update {
                        it.copy(isLoading = false, userInfo = userInfo)
                    }
                    UserManager.instance.login(userInfo)
                    _loginEventChannel.send(LoginEvent.LoginSuccess)
                }
                onFailure { exception ->
                    _loginState.update {
                        it.copy(isLoading = false, userInfo = null)
                    }
                    if (exception is CancellationException) {
                        _loginEventChannel.send(LoginEvent.LoginFailure(errorMsg = "取消登陆"))
                    } else {
                        _loginEventChannel.send(
                            LoginEvent.LoginFailure(
                                errorMsg = exception.message ?: "登录失败"
                            )
                        )
                    }
                }
            }
        }
    }


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.InputUserName -> {
                _loginState.update { state ->
                    state.copy(
                        userName = action.userName
                    )
                }
            }

            is LoginAction.InputPassword -> {
                _loginState.update { state ->
                    state.copy(
                        password = action.password
                    )
                }
            }

            is LoginAction.UpdateIsPasswordVisible -> {
                _loginState.update { state ->
                    state.copy(
                        isPasswordVisible = action.isPasswordVisible
                    )
                }
            }

            is LoginAction.Login -> {
                login()
            }
        }
    }


    fun cancelLogin() {
        _loginState.update {
            it.copy(isLoading = false, userInfo = null)
        }
        loginJob?.cancel()
    }

    fun cancelLogout() {
        _logoutState.update {
            it.copy(isLoading = false)
        }
        logoutJob?.cancel()
    }


    fun logout() {
        logoutJob = viewModelScope.launch {
            _logoutState.update {
                it.copy(isLoading = true)
            }
            loginRepository.logout().apply {
                onSuccess {
                    UserManager.instance.logout()
                    _logoutState.update {
                        it.copy(isLoading = false)
                    }
                    _logoutEventChannel.send(LogoutEvent.LogoutSuccess)
                }
                onFailure { exception ->
                    _logoutState.update {
                        it.copy(isLoading = false)
                    }
                    _logoutEventChannel.send(
                        LogoutEvent.LogoutFailure(
                            exception.message ?: "退出登录失败"
                        )
                    )
                }
            }
        }
    }
}