package com.wanandroid.compose.route

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.main.MainScreen
import com.wanandroid.compose.main.action.ProfileAction
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
            onAction = {
                when (it) {
                    ProfileAction.Coin -> navigator.goTo(RouteNavKey.Coin)
                    ProfileAction.Share -> navigator.goTo(RouteNavKey.Share)
                    ProfileAction.Collect -> navigator.goTo(RouteNavKey.Collect)
                    ProfileAction.Bookmark -> navigator.goTo(RouteNavKey.BookMark)
                    ProfileAction.History -> navigator.goTo(RouteNavKey.History)
                    ProfileAction.Code -> {}
                    ProfileAction.About -> {
                        launchCustomChromeTab(
                            context = context,
                            uri = "https://www.wanandroid.com/".toUri(),
                            toolbarColor = toolbarColor,
                        )
                    }
                    ProfileAction.Setting -> navigator.goTo(RouteNavKey.Settings)
                    ProfileAction.Login -> navigator.goTo(RouteNavKey.Login())
                    ProfileAction.Message -> navigator.goTo(RouteNavKey.Message)
                }
            }
        )
    }
}