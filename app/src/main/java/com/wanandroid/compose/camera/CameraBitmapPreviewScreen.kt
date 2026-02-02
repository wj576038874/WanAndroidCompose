package com.wanandroid.compose.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.res.stringResource
import com.wanandroid.compose.R
import com.wanandroid.compose.common.CommonToolbar

/**
 * Created by wenjie on 2026/01/28.
 */
@Composable
fun CameraBitmapPreviewScreen(
    modifier: Modifier = Modifier,
    byteArray: ByteArray,
    onBackClick: () -> Unit
) {

    Scaffold(
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_book_mark),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = byteArray.decodeToImageBitmap(),
                contentDescription = null,
                modifier = modifier
            )
        }
    }


}