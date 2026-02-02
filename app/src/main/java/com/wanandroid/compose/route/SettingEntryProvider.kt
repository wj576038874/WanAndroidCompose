package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.setting.SettingScreen

/**
 * Created by wenjie on 2026/02/02.
 */

fun EntryProviderScope<NavKey>.forSettingScreen(navigator: Navigator) {
    entry<RouteNavKey.Settings> {
        SettingScreen(
            onLogout = {
                navigator.goBack(RouteNavKey.Settings)
            },
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Settings)
            }
        )
    }
}
