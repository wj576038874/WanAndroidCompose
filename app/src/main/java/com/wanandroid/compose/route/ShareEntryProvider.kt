package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.share.ShareScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forShareScreen(navigator: Navigator) {
    entry<RouteNavKey.Share> {
        ShareScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Share)
            }
        )
    }
}
