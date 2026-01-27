@file:OptIn(ExperimentalMaterial3Api::class)

package com.wanandroid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.wanandroid.compose.login.LoginScreen
import com.wanandroid.compose.main.MainScreen
import com.wanandroid.compose.main.screen.ArticleDetailScreen
import com.wanandroid.compose.route.Route
import com.wanandroid.compose.setting.SettingScreen

/**
 * Created by wenjie on 2026/01/22.
 */

val LocalBackStack = staticCompositionLocalOf<NavBackStack<NavKey>> {
    error("LocalBackStack")
}

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> {
    error("LocalAuthViewModel")
}

@Composable
fun WanAndroidApp(modifier: Modifier = Modifier, themeViewModel: ThemeViewModel) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        val backStack = rememberNavBackStack(Route.Main)
        CompositionLocalProvider(
            LocalBackStack provides backStack,
            LocalThemeViewModel provides themeViewModel,
        ) {
            innerPadding.calculateBottomPadding()
            NavDisplay(
                modifier = modifier,
//                    .padding(bottom = innerPadding.calculateBottomPadding()),
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Route.Main> {
                        MainScreen(
                            onArticleItemClick = {
                                backStack.add(Route.ArticleDetail(articleItem = it))
                            }
                        )
                    }
                    entry<Route.ArticleDetail> {
                        ArticleDetailScreen(articleItem = it.articleItem)
                    }
                    entry<Route.Login> {
                        LoginScreen()
                    }
                    entry<Route.Settings> {
                        SettingScreen()
                    }
                }
            )
        }
    }
}