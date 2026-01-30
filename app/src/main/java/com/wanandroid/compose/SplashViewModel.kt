package com.wanandroid.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/26.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun checkLogin() {
        viewModelScope.launch {
            loginRepository.getUserCoinInfo().apply {
                onSuccess { userInfoData ->
                    userInfoData.userInfo?.let { userInfo ->
                        UserManager.instance.login(userInfo)
                    }
                    _isReady.value = true
                }
                onFailure {
                    _isReady.value = true
                }
            }
        }
    }

}