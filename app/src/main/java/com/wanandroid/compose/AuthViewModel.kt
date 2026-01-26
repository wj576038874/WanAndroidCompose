package com.wanandroid.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.bean.UserInfo
import com.wanandroid.compose.http.LoginApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.main.state.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/26.
 */
class AuthViewModel() : ViewModel() {

    private val _isLogin = MutableStateFlow(false)
    val isLogin: StateFlow<Boolean> = _isLogin.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    /**
     * 检查登录状态
     */
    private val loginApi by lazy {
        RetrofitHelper.create(LoginApi::class.java)
    }

    fun checkLogin() {
        viewModelScope.launch {
            runCatching {
                loginApi.getUserCoinInfo()
            }.onSuccess {
                _isReady.value = true
                _isLogin.value = it.isSuccess
                _userInfo.value = it.data?.userInfo
            }.onFailure {
                _isReady.value = true
                _isLogin.value = false
            }
        }
    }

    fun login() {
        _isLogin.value = true
    }

    fun logout() {
        _isLogin.value = false
    }
}