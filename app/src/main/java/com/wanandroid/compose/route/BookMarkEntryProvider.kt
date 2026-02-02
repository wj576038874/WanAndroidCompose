package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.bookmark.BookMarkScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forBookMarkScreen(navigator: Navigator) {
    entry<RouteNavKey.BookMark> {
        BookMarkScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.BookMark)
            }
        )
    }
}
