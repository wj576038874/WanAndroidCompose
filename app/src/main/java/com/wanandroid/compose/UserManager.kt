package com.wanandroid.compose

import com.wanandroid.compose.bean.UserInfo
import com.wanandroid.compose.http.OkHttpHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by wenjie on 2026/01/26.
 */
class UserManager private constructor() {
    companion object {
        val instance: UserManager by lazy { UserManager() }
    }

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    fun login(userInfo: UserInfo) {
        _userInfo.value = userInfo
        _isLogin.value = true
    }

    fun logout() {
        _isLogin.value = false
        _userInfo.value = null
        OkHttpHelper.instance.clearCookies()
    }

    fun addCollectId(id: Int) {
        val current = _userInfo.value ?: return
        if (id in current.collectIds) return  // 避免重复添加
        val newIds = current.collectIds + id   // + 操作符创建全新 Set
        _userInfo.value = current.copy(collectIds = newIds)
    }

    fun removeCollectId(id: Int) {
        val current = _userInfo.value ?: return
        if (id !in current.collectIds) return  // 避免重复移除
        val newIds = current.collectIds - id   // - 操作符创建全新 Set
        _userInfo.value = current.copy(collectIds = newIds)
    }
}