package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.history.HistoryScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forHistoryScreen(navigator: Navigator) {
    entry<RouteNavKey.History> {
        HistoryScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.History)
            }
        )
    }
}
