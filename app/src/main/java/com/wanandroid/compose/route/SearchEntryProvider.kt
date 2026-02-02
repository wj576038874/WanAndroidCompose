package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.search.SearchScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forSearchScreen(navigator: Navigator) {
    entry<RouteNavKey.Search> {
        SearchScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Search)
            }
        )
    }
}
