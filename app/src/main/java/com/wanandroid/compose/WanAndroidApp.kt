@file:OptIn(ExperimentalMaterial3Api::class)

package com.wanandroid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.wanandroid.compose.locale.AppLocale
import com.wanandroid.compose.route.Navigator
import com.wanandroid.compose.route.RouteNavKey
import com.wanandroid.compose.route.forArticleDetailScreen
import com.wanandroid.compose.route.forBookMarkScreen
import com.wanandroid.compose.route.forCameraBitmapPreviewScreen
import com.wanandroid.compose.route.forCameraScreen
import com.wanandroid.compose.route.forCameraScreen2
import com.wanandroid.compose.route.forCoinScreen
import com.wanandroid.compose.route.forCollectScreen
import com.wanandroid.compose.route.forHistoryScreen
import com.wanandroid.compose.route.forLoginScreen
import com.wanandroid.compose.route.forMainScreen
import com.wanandroid.compose.route.forMessageScreen
import com.wanandroid.compose.route.forSearchScreen
import com.wanandroid.compose.route.forSettingScreen
import com.wanandroid.compose.route.forShareScreen

/**
 * Created by wenjie on 2026/01/22.
 */

//val LocalBackStack = staticCompositionLocalOf<NavBackStack<NavKey>> {
//    error("LocalBackStack")
//}

/**
 * 导航事件 全部统一冒泡到导航最顶层 统一管理
 * 不再向下传递Navigator
 */
//@Deprecated("不再使用")
//val LocalNavigator = staticCompositionLocalOf<Navigator> {
//    error("LocalNavigator")
//}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    error("LocalAuthViewModel")
}

/**
 * 自定义实现应用内语言切换 无需重启activity 且无闪烁
 */
val LocalAppLocale = staticCompositionLocalOf<AppLocale> {
    error("LocalAppLocale")
}

//@Composable
//fun rememberNavigator(): Navigator {
//    val backStack = rememberNavBackStack(RouteNavKey.Main)
//    val navigator = retain {
//        Navigator(
//            backStack = backStack,
//            onNavigateToRestrictedKey = {
//                RouteNavKey.Login(redirectToKey = it)
//            },
//        )
//    }
//    return navigator
//}

@Composable
fun WanAndroidApp(modifier: Modifier = Modifier, appViewModel: AppViewModel) {
//    val language2 by appViewModel.language2.collectAsStateWithLifecycle()
//    val configuration = LocalConfiguration.current
//    if (language2 == "system"){
//        val newLocale = Locale.forLanguageTag(appViewModel.defaultLanguage)
//        Locale.setDefault(newLocale)
//        configuration.setLocale(newLocale)
//    }else{
//        val newLocale = Locale.forLanguageTag(language2)
//        Locale.setDefault(newLocale)
//        configuration.setLocale(newLocale)
//    }
//    val context = LocalContext.current
//    val newContext = context.createConfigurationContext(configuration)

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        val backStack = rememberNavBackStack(RouteNavKey.Main)
        val navigator = remember {
            Navigator(
                backStack = backStack,
                onNavigateToRestrictedKey = {
                    RouteNavKey.Login(redirectToKey = it)
                },
            )
        }
        val appLocale by appViewModel.appLocale.collectAsStateWithLifecycle()
        CompositionLocalProvider(
//            LocalNavigator provides navigator,
//            LocalBackStack provides backStack,
            LocalAppViewModel provides appViewModel,
            LocalAppLocale provides appLocale,
//            LocalContext provides newContext,
        ) {
            innerPadding.calculateBottomPadding()
            NavDisplay(
                modifier = modifier,
//                    .padding(bottom = innerPadding.calculateBottomPadding()),
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    //https://developer.android.com/guide/navigation/navigation-3/naventrydecorators?hl=zh-cn#when-use
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    forMainScreen(navigator)
                    forArticleDetailScreen(navigator)
                    forLoginScreen(navigator)
                    forSettingScreen(navigator)
                    forCoinScreen(navigator)
                    forCollectScreen(navigator)
                    forCameraScreen(navigator)
                    forSearchScreen(navigator)
                    forCameraBitmapPreviewScreen(navigator)
                    forCameraScreen2(navigator)
                    forMessageScreen(navigator)
                    forShareScreen(navigator)
                    forBookMarkScreen(navigator)
                    forHistoryScreen(navigator)
                }
            )
        }
    }
}