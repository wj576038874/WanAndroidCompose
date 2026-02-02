package com.wanandroid.compose.route

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.camera.CameraScreen2

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forCameraScreen2(navigator: Navigator) {
    entry<RouteNavKey.Camera2> {
        CameraScreen2(
            onTakePhoto = {
                navigator.goTo(
                    RouteNavKey.CameraBitmapPreview(
                        byteArray = it
                    )
                )
            }
        )
    }
}
