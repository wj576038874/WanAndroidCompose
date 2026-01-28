package com.wanandroid.compose.main.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.main.api.HomeApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.main.repository.HomeRepository
import com.wanandroid.compose.main.viemodel.HomeViewModel
import com.wanandroid.compose.utils.launchCustomChromeTab
import kotlinx.coroutines.delay

/**
 * Created by wenjie on 2026/01/22.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onArticleItemClick: (ArticleItem) -> Unit
) {
    Log.e("asd", "HomeScreen$innerPadding")
    val viewModel = viewModel {
        val homeApi = RetrofitHelper.create(HomeApi::class.java)
        val homeRepository = HomeRepository(homeApi = homeApi)
        HomeViewModel(homeRepository = homeRepository)
    }
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PullToRefreshBox(
            isRefreshing = homeUiState.isLoading, onRefresh = {
                viewModel.getHomeData(pageNum = 0)
            }) {
            val listState = rememberLazyListState()
            var page by remember { mutableIntStateOf(0) }
            LaunchedEffect(listState) {
                snapshotFlow {
                    listState.layoutInfo
                }.collect { layoutInfo ->
                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    val lastVisible =
                        lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 1
                    if (lastVisible && !homeUiState.noMoreData) {
                        page++
                        viewModel.loadNexPage(pageNum = page)
                    }
                }
            }
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                homeUiState.bannerList?.let {
                    item {
                        Banner(bannerList = it)
                    }
                }
                homeUiState.articleList?.let { item ->
                    items(
                        items = item,
                        key = { item ->
                            item.id
                        }
                    ) { item ->
                        ArticleItemCard(
                            articleItem = item,
                            modifier = Modifier,
                            onArticleItemClick = onArticleItemClick,
                            onCollectClick = {
                                if (it) {
                                    viewModel.collectArticle(id = item.id)
                                } else {
                                    viewModel.unCollectArticle(id = item.id)
                                }
                            }
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (homeUiState.noMoreData) {
                                Text(
                                    text = "No more data",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                                Text(
                                    text = "Loading...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItemCard(
    articleItem: ArticleItem,
    modifier: Modifier = Modifier,
    onArticleItemClick: (ArticleItem) -> Unit,
    onCollectClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .wrapContentHeight()
            .clickable {
                onArticleItemClick(articleItem)
            }) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = articleItem.shareUser.ifBlank { articleItem.author },
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
                Text(
                    text = articleItem.niceShareDate,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = articleItem.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${articleItem.chapterName} & ${articleItem.superChapterName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
            )
        }
        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                onCollectClick(!articleItem.collect)
            }
        ) {
            AnimatedContent(
                targetState = articleItem.collect
            ) {
                Icon(
                    imageVector = if (it) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun ArticleItemCardPreview() {
    ArticleItemCard(
        articleItem = ArticleItem(
            id = 0,
            title = "你可能没有那么了解 RecycleView",
            link = "link",
            niceDate = "1天前",
            author = "panoogunker@gmail.com",
            shareUser = "panoogunker@gmail.com",
            shareDate = 0,
            niceShareDate = "1天前",
            superChapterName = "广场Tab",
            chapterName = "自助",
            desc = "",
            collect = false,
        ), modifier = Modifier, onArticleItemClick = {}, onCollectClick = {}
    )
}


@Composable
fun Banner(
    bannerList: List<BannerItem>, modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(0) {
        Int.MAX_VALUE
    }
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary.toArgb()
    LaunchedEffect(true) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }
    HorizontalPager(
        state = pagerState, modifier = modifier
            .fillMaxSize()
            .height(200.dp)
    ) {
        val position = it % bannerList.size
        AsyncImage(
            model = bannerList[position].imagePath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    launchCustomChromeTab(
                        context = context,
                        uri = bannerList[position].url.toUri(),
                        toolbarColor = color
                    )
                },
            contentScale = ContentScale.Crop
        )
    }
}