package com.wanandroid.compose.bookmark

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wanandroid.compose.R
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.route.RouteNavKey

/**
 * Created by wenjie on 2026/01/30.
 */
@Composable
fun BookMarkScreen(modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_book_mark),
                routeNavKey = RouteNavKey.BookMark
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.string_book_mark),
            )
        }
    }
}