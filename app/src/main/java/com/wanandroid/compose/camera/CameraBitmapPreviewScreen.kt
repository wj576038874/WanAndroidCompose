package com.wanandroid.compose.camera

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Created by wenjie on 2026/01/28.
 */
@Composable
fun CameraBitmapPreviewScreen(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap
) {
    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = modifier
    )
}