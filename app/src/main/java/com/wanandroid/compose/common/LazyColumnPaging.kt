package com.wanandroid.compose.common

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.wanandroid.compose.WanAndroidApplication.Companion.context

/**
 * Created by wenjie on 2026/01/28.
 */

@Composable
fun <T: Any> LazyColumnPaging(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<T>,
    content: LazyListScope.() -> Unit,
) {
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = lazyPagingItems::refresh
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            content()
            lazyPagingItems.loadState.apply {
                when {
                    refresh is LoadState.Loading -> {
                        Log.e("asd", "refresh loading")
                    }
                    refresh is LoadState.Error -> {
                        Log.e("asd", "refresh error")
                        Toast.makeText(
                            context,
                            (refresh as LoadState.Error).error.message ?: "refresh error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    append is LoadState.Loading -> {
                        Log.e("asd", "append loading")
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                    append is LoadState.Error -> {
                        Log.e("asd", "append error")
                        item {
                            Text(
                                text = "load more error",
                                modifier = Modifier
                                    .clickable {
                                        lazyPagingItems.retry()
                                    }
                                    .padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    // 当没有更多数据时
                    append.endOfPaginationReached && lazyPagingItems.itemCount > 0 -> {
                        Log.e("asd", "已经到底啦")
                        item {
                            Text(
                                text = "已经到底啦～",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}