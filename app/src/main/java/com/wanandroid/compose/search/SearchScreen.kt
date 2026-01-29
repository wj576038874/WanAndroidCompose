package com.wanandroid.compose.search

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
 * Created by wenjie on 2026/01/28.
 */
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CommonToolbar(
                routeNavKey = RouteNavKey.Search,
                title = "搜索",
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "搜索",
            )
        }
    }
}