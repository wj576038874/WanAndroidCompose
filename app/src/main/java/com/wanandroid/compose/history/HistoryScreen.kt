package com.wanandroid.compose.history

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.SubcomposeAsyncImage
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.utils.ObserveAsEvents
import com.wanandroid.compose.utils.launchCustomChromeTab

/**
 * 阅读历史页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary
    val viewModel = hiltViewModel<HistoryViewModel>()

    val snackbarHostState = remember { SnackbarHostState() }
    val lazyPagingItems = viewModel.historyList.collectAsLazyPagingItems()
    val deleteId by viewModel.deleteIdState.collectAsStateWithLifecycle()

    ObserveAsEvents(
        flow = viewModel.historyEvent,
        onEvent = { event ->
            when (event) {
                is HistoryEvent.Success -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
                is HistoryEvent.Error -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CommonToolbar(
                title = stringResource(id = R.string.string_history),
                onBackClick = onBackClick,
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
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { item ->
                    item.id
                }
            ) { index ->
                val item = lazyPagingItems[index] ?: return@items
                var animateDelete by rememberSaveable(item.id) { mutableStateOf(false) }
                LaunchedEffect(deleteId) {
                    if (deleteId == item.id) {
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
                    HistoryItem(
                        modifier = Modifier,
                        articleItem = item,
                        onArticleItemClick = {
                            launchCustomChromeTab(
                                context = context,
                                uri = item.link.toUri(),
                                toolbarColor = toolbarColor.toArgb()
                            )
                        },
                        onDeleteClick = {
                            viewModel.deleteHistory(item.id)
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    articleItem: ArticleItem,
    onArticleItemClick: (ArticleItem) -> Unit,
    onDeleteClick: () -> Unit
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
        val (author, date, image, title, desc, chapterName, delete) = createRefs()
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
                    top.linkTo(delete.top)
                    start.linkTo(image.start)
                    bottom.linkTo(delete.bottom)
                }
        )
        val barrier = createBottomBarrier(
            image, desc, title
        )
        IconButton(
            modifier = Modifier.constrainAs(delete) {
                top.linkTo(barrier)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            onClick = {
                onDeleteClick()
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.string_delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryItemPreview(modifier: Modifier = Modifier) {
    HistoryItem(
        modifier = modifier,
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
            envelopePic = "https://wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
            originId = 0,
            collect = false,
        ),
        onArticleItemClick = {

        },
        onDeleteClick = {

        }
    )
}
