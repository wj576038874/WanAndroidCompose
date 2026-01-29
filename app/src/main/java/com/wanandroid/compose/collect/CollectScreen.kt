package com.wanandroid.compose.collect

import android.text.Html
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.SubcomposeAsyncImage
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.route.RouteNavKey
import com.wanandroid.compose.utils.launchCustomChromeTab

/**
 * Created by wenjie on 2026/01/28.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary
    val viewmodel = viewModel {
        CollectViewModel(
            collectRepository = CollectRepository(
                collectApi = RetrofitHelper.create(
                    CollectApi::class.java
                )
            )
        )
    }
    val lazyPagingItems = viewmodel.collectList.collectAsLazyPagingItems()
    val unCollectId by viewmodel.unCollectIdState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CommonToolbar(
                routeNavKey = RouteNavKey.Collect,
                title = stringResource(id = R.string.string_collect),
            )
        },
    ) { innerPadding ->
        LazyColumnPaging(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            lazyPagingItems = lazyPagingItems,
        ) {
            items(
                count = lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { item ->
                    item.id
                }) { index ->
                val item = lazyPagingItems[index] ?: return@items
                var animateDelete by rememberSaveable(item.originId) { mutableStateOf(false) }
                LaunchedEffect(unCollectId) {
                    if (unCollectId == item.originId) {
                        animateDelete = true
                    }
                }
                AnimatedVisibility(
                    visible = !animateDelete,
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ) + fadeOut(animationSpec = tween(300)),
                ) {
                    CollectionItem(
                        modifier = Modifier
//                        .animateItem(
//                            fadeInSpec = tween(400),
//                            fadeOutSpec = tween(500, easing = LinearOutSlowInEasing),
//                            placementSpec = spring(
//                                dampingRatio = Spring.DampingRatioNoBouncy,
//                                stiffness = Spring.StiffnessMedium
//                            )
//                        )
                            .padding(horizontal = 16.dp),
                        articleItem = item,
                        onArticleItemClick = {
                            launchCustomChromeTab(
                                context = context,
                                uri = item.link.toUri(),
                                toolbarColor = toolbarColor.toArgb()
                            )
                        },
                        onCollectClick = {
                            viewmodel.unCollectArticle(item.originId)
                        })
                }
            }
        }
    }
}


@Composable
fun CollectionItem(
    modifier: Modifier = Modifier,
    articleItem: ArticleItem,
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
                    text = articleItem.author.ifBlank { "匿名" },
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
                Text(
                    text = articleItem.niceDate,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!articleItem.envelopePic.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .width(120.dp)
                            .height(90.dp)
                            .padding(end = 12.dp),
                        model = articleItem.envelopePic,
                        loading = {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        error = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.tertiary),
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                contentDescription = null
                            )
                        },
                        success = {
                            Image(
                                painter = it.painter,
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        },
                        contentDescription = null
                    )
//                    AsyncImage(
//                        model = articleItem.envelopePic,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(end = 12.dp)
//                            .width(120.dp)
//                            .height(90.dp),
//                        contentScale = ContentScale.Crop
//                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = articleItem.title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (articleItem.desc.isNotBlank()) {
                        Text(
                            text = Html.fromHtml(articleItem.desc, 0).toString(),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = articleItem.chapterName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
            )
        }
        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd), onClick = {
                onCollectClick(!articleItem.collect)
            }) {
            AnimatedContent(
                targetState = articleItem.collect
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
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
fun CollectionItemPreview(modifier: Modifier = Modifier) {
    CollectionItem(
        modifier = modifier, articleItem = ArticleItem(
            id = 0,
            title = "Android图片选择器支持GIF PhotoSelector",
            link = "link",
            niceDate = "1天前",
            author = "panoogunker@gmail.com",
            shareUser = "panoogunker@gmail.com",
            shareDate = 0,
            niceShareDate = "1天前",
            superChapterName = "广场Tab",
            chapterName = "自助",
            envelopePic = "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
            collect = false,
            originId = 0,
            desc = "Android图片选择器支持GIF预览，选择，仿微信的图片选择器的样式和效果。可横竖屏切换显示, 自定义配置，单选，多选，是否显示拍照，material design风格，单选裁剪，拍照裁剪，滑动翻页预览，双击放大，缩放"
        ), onArticleItemClick = {}, onCollectClick = {})
}