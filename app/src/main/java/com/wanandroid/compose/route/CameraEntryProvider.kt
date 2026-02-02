package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.camera.CameraScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forCameraScreen(navigator: Navigator) {
    entry<RouteNavKey.Camera> {
        CameraScreen(
            onTakePhoto = { byteArray ->
                navigator.goTo(
                    RouteNavKey.CameraBitmapPreview(
                        byteArray = byteArray
                    )
                )
            }
        )
    }
}
