package com.wanandroid.compose.camera

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.decodeToImageBitmap

/**
 * Created by wenjie on 2026/01/28.
 */
@Composable
fun CameraBitmapPreviewScreen(
    modifier: Modifier = Modifier,
    byteArray: ByteArray
) {
    Image(
        bitmap = byteArray.decodeToImageBitmap(),
        contentDescription = null,
        modifier = modifier
    )
}