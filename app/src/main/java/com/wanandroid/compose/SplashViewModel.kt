package com.wanandroid.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.login.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/26.
 */
class SplashViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun checkLogin() {
        viewModelScope.launch {
            runCatching {
                loginRepository.getUserCoinInfo()
            }.onSuccess {
                val userInfo = it.data?.userInfo
                userInfo?.let {
                    UserManager.instance.login(userInfo)
                }
                _isReady.value = true
            }.onFailure {
                _isReady.value = true
            }
        }
    }

}