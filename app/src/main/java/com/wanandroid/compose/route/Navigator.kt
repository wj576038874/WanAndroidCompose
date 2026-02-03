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
    private val userInfo = UserManager.instance.userInfo

    /**
     * 如果跳转的页面routeNavKey 需要登录 且 未登录 则跳转到登录页
     * 然后把routeNavKey 作为onNavigateToRestrictedKey方法的参数 调用onNavigateToRestrictedKey方法返回登陆页面NavKey
     * 且这个登陆的NavKey的redirectToKey 就是 我们需要跳转的目标页面
     */
    fun goTo(routeNavKey: RouteNavKey) {
        val isLogin = userInfo.value != null
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
