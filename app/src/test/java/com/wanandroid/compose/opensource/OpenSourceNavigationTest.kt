package com.wanandroid.compose.opensource

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.route.Navigator
import com.wanandroid.compose.route.RouteNavKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OpenSourceNavigationTest {
    @Test fun `anonymous user opens the library page and returns without a login redirect`() {
        Dispatchers.setMain(StandardTestDispatcher())
        try {
            assertNull(UserManager.instance.userInfo.value)
            val stack = NavBackStack<NavKey>(RouteNavKey.Main)
            val navigator = Navigator(stack) { error("Open source must not request login") }
            navigator.goTo(RouteNavKey.OpenSource)
            assertEquals(listOf(RouteNavKey.Main, RouteNavKey.OpenSource), stack.toList())
            navigator.goBack()
            assertEquals(listOf(RouteNavKey.Main), stack.toList())
        } finally {
            Dispatchers.resetMain()
        }
    }
}
