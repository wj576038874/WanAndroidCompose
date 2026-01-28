package com.wanandroid.compose.coin

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.R
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.coin.CoinApi
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
    val lazyPagingItems = viewModel.coinList.collectAsLazyPagingItems()
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
        LazyColumnPaging(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            lazyPagingItems = lazyPagingItems,
            content = {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { item ->
                        item.id
                    }
                ) { index ->
                    lazyPagingItems[index]?.let { item ->
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
            }
        )
    }
}