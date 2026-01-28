@file:OptIn(ExperimentalMaterial3Api::class)

package com.wanandroid.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.wanandroid.compose.camera.CameraScreen
import com.wanandroid.compose.coin.CoinScreen
import com.wanandroid.compose.collect.CollectScreen
import com.wanandroid.compose.login.LoginScreen
import com.wanandroid.compose.main.MainScreen
import com.wanandroid.compose.main.screen.ArticleDetailScreen
import com.wanandroid.compose.route.Route
import com.wanandroid.compose.search.SearchScreen
import com.wanandroid.compose.setting.SettingScreen
import java.util.Locale

/**
 * Created by wenjie on 2026/01/22.
 */

val LocalBackStack = staticCompositionLocalOf<NavBackStack<NavKey>> {
    error("LocalBackStack")
}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    error("LocalAuthViewModel")
}

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
        val backStack = rememberNavBackStack(Route.Main)
        CompositionLocalProvider(
            LocalBackStack provides backStack,
            LocalAppViewModel provides appViewModel,
//            LocalContext provides newContext,
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
                    entry<Route.Login>(
                        metadata = NavDisplay.transitionSpec {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(800)
                            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                        } + NavDisplay.popTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(800)
                                    )
                        } + NavDisplay.predictivePopTransitionSpec {
                            // Slide old content down, revealing the new content in place underneath
                            EnterTransition.None togetherWith
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(800)
                                    )
                        }
                    ) {
                        LoginScreen()
                    }
                    entry<Route.Settings> {
                        SettingScreen()
                    }
                    entry<Route.Coin> {
                        CoinScreen()
                    }
                    entry<Route.Collect> {
                        CollectScreen()
                    }
                    entry<Route.Camera> {
                        CameraScreen()
                    }
                    entry<Route.Search> {
                        SearchScreen()
                    }
                }
            )
        }
    }
}