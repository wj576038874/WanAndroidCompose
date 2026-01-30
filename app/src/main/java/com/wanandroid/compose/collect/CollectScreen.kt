package com.wanandroid.compose.collect

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.SubcomposeAsyncImage
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.common.LazyColumnPaging
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
                        modifier = Modifier,
//                        .animateItem(
//                            fadeInSpec = tween(400),
//                            fadeOutSpec = tween(500, easing = LinearOutSlowInEasing),
//                            placementSpec = spring(
//                                dampingRatio = Spring.DampingRatioNoBouncy,
//                                stiffness = Spring.StiffnessMedium
//                            )
//                        )
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
            desc = "<p>可以从常见出问题场景、检测方案等方面回答。</p>"
//            desc = "Android图片选择器支持GIF预览，选择，仿微信的图片选择器的样式和效果。可横竖屏切换显示, 自定义配置，单选，多选，是否显示拍照，material design风格，单选裁剪，拍照裁剪，滑动翻页预览，双击放大，缩放"
        ), onArticleItemClick = {}, onCollectClick = {})
}

@Composable
fun CollectionItem(
    modifier: Modifier = Modifier,
    articleItem: ArticleItem,
    onArticleItemClick: (ArticleItem) -> Unit,
    onCollectClick: (Boolean) -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onArticleItemClick(articleItem)
            }
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
    ) {
        val (author, date, image, title, desc, chapterName, collect) = createRefs()
        Text(
            text = articleItem.author.ifBlank { "匿名" },
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(author) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = articleItem.niceDate,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .constrainAs(date) {
                    end.linkTo(parent.end)
                    top.linkTo(author.top)
                    bottom.linkTo(author.bottom)
                }
        )
        val barrierAuthor = createBottomBarrier(
            author, date, margin = 12.dp
        )
        if (!articleItem.envelopePic.isNullOrBlank()) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(barrierAuthor)
                        start.linkTo(parent.start)
                        end.linkTo(title.start, 8.dp)
                        width = Dimension.value(120.dp)
                        height = Dimension.value(90.dp)
                    },
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
//            AsyncImage(
//                model = articleItem.envelopePic,
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(end = 12.dp)
//                    .width(120.dp)
//                    .height(90.dp),
//                contentScale = ContentScale.Crop
//            )
        }
        Text(
            text = articleItem.title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(barrierAuthor)
                start.linkTo(image.end)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        if (articleItem.desc.isNotBlank()) {
            Text(
                text = HtmlCompat.fromHtml(articleItem.desc, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    .toString(),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(desc) {
                    top.linkTo(title.bottom, 4.dp)
                    start.linkTo(title.start)
                    end.linkTo(title.end)
                    width = Dimension.fillToConstraints
                }
            )
        }
        Text(
            text = articleItem.chapterName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .constrainAs(chapterName) {
                    top.linkTo(collect.top)
                    start.linkTo(image.start)
                    bottom.linkTo(collect.bottom)
                }
        )
        val barrier = createBottomBarrier(
            image, desc, title
        )
        IconButton(
            modifier = Modifier.constrainAs(collect) {
                top.linkTo(barrier)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            onClick = {
                onCollectClick(!articleItem.collect)
            }
        ) {
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