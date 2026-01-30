package com.wanandroid.compose.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.main.api.NavigationApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.main.repository.NavigationRepository
import com.wanandroid.compose.main.viemodel.NavigationViewModel
import com.wanandroid.compose.utils.launchCustomChromeTab

/**
 * Created by wenjie on 2026/01/22.
 */
@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    val viewModel = hiltViewModel<NavigationViewModel>()

    val navigationUiState by viewModel.navigationUiState.collectAsStateWithLifecycle()

    val itemList = navigationUiState.navigationList

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(innerPadding.calculateTopPadding())
                .background(
                    color = (if (itemList.isEmpty()) {
                        Color.Transparent
                    } else {
                        MaterialTheme.colorScheme.primary
                    })
                )
        )
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onRefresh = {
                viewModel.getNavigationList()
            },
            isRefreshing = navigationUiState.isLoading
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ) {
                itemList.forEachIndexed { _, navigationItem ->
                    stickyHeader {
                        NavigationHeader(
                            title = navigationItem.name
                        )
                    }
                    item(navigationItem.cid) {
                        NavigationItem(articles = navigationItem.articles)
                    }
                }
            }
        }
    }

}

@Composable
fun NavigationItem(
    modifier: Modifier = Modifier, articles: List<ArticleItem>
) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary.toArgb()
    FlowRow(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        articles.forEach { articleItem ->
            FilterChip(
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    labelColor = MaterialTheme.colorScheme.onSecondary,
                ),
                selected = false,
                onClick = {
                    launchCustomChromeTab(
                        context = context,
                        uri = articleItem.link.toUri(),
                        toolbarColor = color
                    )
                },
                label = {
                    Text(text = articleItem.title)
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun NavigationHeader(
    modifier: Modifier = Modifier,
    title: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}