package com.wanandroid.compose.coin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.R
import com.wanandroid.compose.WanAndroidApplication.Companion.context
import com.wanandroid.compose.http.CoinApi
import com.wanandroid.compose.http.RetrofitHelper

/**
 * Created by wenjie on 2026/01/27.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinScreen(modifier: Modifier = Modifier) {
    val backStack = LocalBackStack.current
    val viewModel = viewModel {
        val coinApi = RetrofitHelper.create(CoinApi::class.java)
        val coinRepository = CoinRepository(coinApi)
        CoinViewModel(coinRepository)
    }
    val itemsList = viewModel.coinList.collectAsLazyPagingItems()
    val isRefreshing = itemsList.loadState.refresh is LoadState.Loading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(
                        text = stringResource(id = R.string.string_settings),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = {
                            backStack.removeLastOrNull()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                })
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = isRefreshing,
            onRefresh = itemsList::refresh
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    count = itemsList.itemCount,
                    key = itemsList.itemKey { item ->
                        item.id
                    }
                ) { index ->
                    itemsList[index]?.let { item ->
                        Text(
                            text = item.desc,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Log.e("asd", "click ${item.desc}")
                                }
                                .padding(16.dp)

                        )
                    }
                }
                itemsList.loadState.apply {
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
                                        modifier = Modifier.padding(
                                            top = 8.dp,
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = innerPadding.calculateBottomPadding() + 8.dp
                                        )
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
                                            itemsList.retry()
                                        }
                                        .padding(
                                            top = 8.dp,
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = innerPadding.calculateBottomPadding() + 8.dp
                                        ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        // 当 over=true 且没有更多数据时
                        append.endOfPaginationReached && itemsList.itemCount > 0 -> {
                            Log.e("asd", "已经到底啦")
                            item {
                                Text(
                                    text = "已经到底啦～",
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        start = 8.dp,
                                        end = 8.dp,
                                        bottom = innerPadding.calculateBottomPadding() + 8.dp
                                    ),
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
}