package com.wanandroid.compose.route

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.UserManager

/**
 * Created by wenjie on 2026/01/29.
 */

class Navigator(val backStack: NavBackStack<NavKey>) {

    private val isLogin = UserManager.instance.isLogin

    private val needLoginRoutes = listOf(
        Route.Coin,
        Route.Collect,
    )

    fun goTo(destination: Route) {
        val isLogin = isLogin.value
        if (needLoginRoutes.contains(destination) && !isLogin) {
            backStack.add(Route.Login(destination = destination))
            return
        }
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }

    fun goBack(route: Route) {
        backStack.remove(route)
    }
}
