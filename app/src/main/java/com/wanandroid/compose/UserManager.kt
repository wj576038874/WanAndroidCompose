package com.wanandroid.compose

import com.wanandroid.compose.bean.UserInfo
import com.wanandroid.compose.http.OkHttpHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by wenjie on 2026/01/26.
 */
class UserManager private constructor() {
    companion object {
        val instance: UserManager by lazy { UserManager() }
    }

    private val scope = MainScope()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    val isLogin = userInfo.map {
        it != null
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    fun login(userInfo: UserInfo) {
        _userInfo.value = userInfo
    }

    fun logout() {
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