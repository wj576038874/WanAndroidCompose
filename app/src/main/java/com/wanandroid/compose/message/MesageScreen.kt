package com.wanandroid.compose.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.route.RouteNavKey

/**
 * Created by wenjie on 2026/01/30.
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CommonToolbar(
                title = "消息",
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
            Text(
                text = "消息",
            )
        }
    }
}