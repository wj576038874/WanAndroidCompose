package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.message.MessageScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forMessageScreen(navigator: Navigator) {
    entry<RouteNavKey.Message> {
        MessageScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Message)
            }
        )
    }
}
