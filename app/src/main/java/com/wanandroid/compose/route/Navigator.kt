package com.wanandroid.compose.route

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.UserManager

/**
 * Created by wenjie on 2026/01/29.
 */

class Navigator(
    private val backStack: NavBackStack<NavKey>,
    private val onNavigateToRestrictedKey: (targetKey: RouteNavKey?) -> RouteNavKey,
) {
    private val isLogin = UserManager.instance.isLogin

    fun goTo(routeNavKey: RouteNavKey) {
        val isLogin = isLogin.value
        if (routeNavKey.requiresLogin && !isLogin) {
            //把目标存放到登录页 后续登录成功后 可以拿到这个目标页面 进行跳转
            val loginKey = onNavigateToRestrictedKey(routeNavKey)
            backStack.add(loginKey)
        } else {
            backStack.add(routeNavKey)
        }
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun goBack(routeNavKey: RouteNavKey) {
        backStack.remove(routeNavKey)
    }
}
