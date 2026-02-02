package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.collect.CollectScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forCollectScreen(navigator: Navigator) {
    entry<RouteNavKey.Collect> {
        CollectScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Collect)
            }
        )
    }
}
