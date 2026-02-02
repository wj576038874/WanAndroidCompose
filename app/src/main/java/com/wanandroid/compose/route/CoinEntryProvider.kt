package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.coin.CoinScreen

/**
 * Created by wenjie on 2026/02/02.
 */

fun EntryProviderScope<NavKey>.forCoinScreen(navigator: Navigator) {
    entry<RouteNavKey.Coin> {
        CoinScreen(
            onBackClick = {
                navigator.goBack(routeNavKey = RouteNavKey.Coin)
            }
        )
    }
}
