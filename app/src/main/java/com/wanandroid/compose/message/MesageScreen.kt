package com.wanandroid.compose.message

import android.text.Html
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.MessageItem
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.utils.ObserveAsEvents
import com.wanandroid.compose.utils.launchCustomChromeTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by wenjie on 2026/01/30.
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel<MessageViewModel>()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val isLoadingCount by viewModel.isLoadingCount.collectAsStateWithLifecycle()
    val unreadMessages = viewModel.unreadMessageList.collectAsLazyPagingItems()
    val readMessages = viewModel.readMessageList.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    ObserveAsEvents(
        flow = viewModel.messageEvent,
        onEvent = { message ->
            snackbarHostState.showSnackbar(message)
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_message),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        viewModel.refreshUnreadCount()
                    },
                    text = {
                        Text(
                            text = if (isLoadingCount) {
                                stringResource(R.string.string_unread)
                            } else {
                                "${stringResource(R.string.string_unread)} ($unreadCount)"
                            }
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                    },
                    text = {
                        Text(text = stringResource(R.string.string_read))
                    }
                )
            }

            if (selectedTabIndex == 0) {
                MessageList(
                    lazyPagingItems = unreadMessages,
                    emptyText = stringResource(R.string.string_message_empty_unread),
                )
            } else {
                MessageList(
                    lazyPagingItems = readMessages,
                    emptyText = stringResource(R.string.string_message_empty_read),
                )
            }
        }
    }
}

@Composable
private fun MessageList(
    lazyPagingItems: LazyPagingItems<MessageItem>,
    emptyText: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    val isEmpty =
        lazyPagingItems.itemCount == 0 && lazyPagingItems.loadState.refresh !is LoadState.Loading

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumnPaging(
            modifier = Modifier.fillMaxSize(),
            lazyPagingItems = lazyPagingItems,
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { item -> item.id }
            ) { index ->
                val item = lazyPagingItems[index] ?: return@items
                MessageCard(
                    item = item,
                    onClick = {
                        val target = item.fullLink ?: item.link
                        if (!target.isNullOrBlank()) {
                            launchCustomChromeTab(
                                context = context,
                                uri = target.toUri(),
                                toolbarColor = toolbarColor,
                            )
                        }
                    }
                )
            }
        }

        if (isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun MessageCard(
    item: MessageItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (titleRef, dateRef, authorRef, statusRef, contentRef) = createRefs()

            Text(
                text = item.title.orEmpty().ifBlank {
                    item.tag.orEmpty().ifBlank { stringResource(R.string.string_message) }
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(dateRef.start, 12.dp)
                    width = Dimension.fillToConstraints
                }
            )
            Text(
                text = formatMessageDate(item),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.constrainAs(dateRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            )
            Text(
                text = item.fromUser.orEmpty().ifBlank {
                    item.category.orEmpty().ifBlank { "WanAndroid" }
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(authorRef) {
                    top.linkTo(titleRef.bottom, 6.dp)
                    start.linkTo(parent.start)
                    end.linkTo(statusRef.start, 12.dp)
                    width = Dimension.fillToConstraints
                }
            )
            Text(
                text = if (item.isRead == 1) {
                    stringResource(R.string.string_read)
                } else {
                    stringResource(R.string.string_unread)
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (item.isRead == 1) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    Color(0xFFD32F2F)
                },
                modifier = Modifier.constrainAs(statusRef) {
                    top.linkTo(authorRef.top)
                    bottom.linkTo(authorRef.bottom)
                    end.linkTo(parent.end)
                }
            )
            Text(
                text = buildMessagePreview(item),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(contentRef) {
                    top.linkTo(authorRef.bottom, 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}

private fun buildMessagePreview(item: MessageItem): String {
    val message = Html.fromHtml(item.message.orEmpty(), 0).toString().trim()
    if (message.isNotBlank()) {
        return message
    }
    return item.link.orEmpty().ifBlank { item.fullLink.orEmpty() }
}

private fun formatMessageDate(item: MessageItem): String {
    if (!item.niceDate.isNullOrBlank()) {
        return item.niceDate
    }
    if (item.date <= 0L) {
        return ""
    }
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(item.date))
}

@Preview(showBackground = true)
@Composable
fun MessageCardPreview(modifier: Modifier = Modifier) {
    MessageCard(
        item = MessageItem(
            category = "category",
            fromUser = "admin",
            date = System.currentTimeMillis(),
        ),
        onClick = {},
        modifier = modifier
    )
}
