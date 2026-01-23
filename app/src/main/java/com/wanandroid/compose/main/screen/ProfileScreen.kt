package com.wanandroid.compose.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Created by wenjie on 2026/01/22.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    Box(
        modifier = modifier.fillMaxSize()
            .background(Color.Green)
    )
}