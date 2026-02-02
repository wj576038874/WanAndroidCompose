package com.wanandroid.compose.route

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.main.MainScreen
import com.wanandroid.compose.utils.launchCustomChromeTab

/**
 * Created by wenjie on 2026/02/02.
 */

fun EntryProviderScope<NavKey>.forMainScreen(navigator: Navigator) {
    entry<RouteNavKey.Main> {
        val context = LocalContext.current
        val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
        MainScreen(
            onArticleItemClick = {
                navigator.goTo(RouteNavKey.ArticleDetail(articleItem = it))
            },
            onSearchClick = {
                navigator.goTo(RouteNavKey.Search)
            },
            onCameraClick = {
                navigator.goTo(RouteNavKey.Camera)
            },
            toLogin = {
                navigator.goTo(RouteNavKey.Login())
            },
            toMessage = {
                navigator.goTo(RouteNavKey.Message)
            },
            onCoinClick = {
                navigator.goTo(RouteNavKey.Coin)
            },
            onShareClick = {
                navigator.goTo(RouteNavKey.Share)
            },
            onCollectClick = {
                navigator.goTo(RouteNavKey.Collect)
            },
            onBookMarkClick = {
                navigator.goTo(RouteNavKey.BookMark)
            },
            onHistoryClick = {
                navigator.goTo(RouteNavKey.History)
            },
            onCodeClick = {

            },
            onAboutClick = {
                launchCustomChromeTab(
                    context = context,
                    uri = "https://www.wanandroid.com/".toUri(),
                    toolbarColor = toolbarColor,
                )
            },
            onSettingsClick = {
                navigator.goTo(RouteNavKey.Settings)
            },
        )
    }
}