package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.camera.CameraBitmapPreviewScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forCameraBitmapPreviewScreen(navigator: Navigator) {
    entry<RouteNavKey.CameraBitmapPreview> {
        CameraBitmapPreviewScreen(byteArray = it.byteArray)
    }
}
