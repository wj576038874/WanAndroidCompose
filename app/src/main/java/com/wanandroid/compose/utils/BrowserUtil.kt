package com.wanandroid.compose.utils

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Created by wenjie on 2026/01/27.
 */
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
//        .setSecondaryToolbarColor(Color.Green.toArgb())
//        .setNavigationBarColor(Color.Blue.toArgb())
//        .setNavigationBarDividerColor(Color.Yellow.toArgb())
        .build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setSendToExternalDefaultHandlerEnabled(false)
//        .setShareState(SHARE_STATE_OFF)
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    customTabsIntent.launchUrl(context, uri)
}
