package com.wanandroid.compose.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.wanandroid.compose.R
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.common.LoadingDialog
import com.wanandroid.compose.share.event.ShareEvent
import com.wanandroid.compose.utils.ObserveAsEvents
import com.wanandroid.compose.utils.launchCustomChromeTab
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/30.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel<ShareViewModel>()
    val lazyPagingItems = viewModel.shareList.collectAsLazyPagingItems()
    val deletedId by viewModel.deletedId.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    val addFailedMsg = stringResource(R.string.string_share_add_invalid)
    val addSuccessMsg = stringResource(R.string.string_share_add_success)
    val deleteSuccessMsg = stringResource(R.string.string_share_delete_success)
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var shareTitle by rememberSaveable { mutableStateOf("") }
    var shareLink by rememberSaveable { mutableStateOf("") }

    ObserveAsEvents(
        flow = viewModel.shareEvent,
        onEvent = { event ->
            when (event) {
                is ShareEvent.Message -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                ShareEvent.AddSuccess -> {
                    showAddDialog = false
                    shareTitle = ""
                    shareLink = ""
                    lazyPagingItems.refresh()
                    snackbarHostState.showSnackbar(addSuccessMsg)
                }

                ShareEvent.DeleteSuccess -> {
                    snackbarHostState.showSnackbar(deleteSuccessMsg)
                }
            }
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_share),
                actions = {
                    IconButton(
                        onClick = {
                            showAddDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.string_share_add),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumnPaging(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            lazyPagingItems = lazyPagingItems,
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { item ->
                    item.id
                },
            ) { index ->
                val item = lazyPagingItems[index] ?: return@items
                var animateDelete by rememberSaveable(item.id) { mutableStateOf(false) }
                LaunchedEffect(deletedId) {
                    if (deletedId == item.id) {
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
                    ShareItem(
                        item = item,
                        onClick = {
                            if (!item.link.isNullOrBlank()) {
                                launchCustomChromeTab(
                                    context = context,
                                    uri = item.link.toUri(),
                                    toolbarColor = toolbarColor,
                                )
                            }
                        },
                        onDeleteClick = {
                            viewModel.deleteShareArticle(item.id)
                        }
                    )
                }
            }
            if (lazyPagingItems.itemCount == 0 && lazyPagingItems.loadState.refresh !is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Text(
                            text = stringResource(R.string.string_share_empty),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                },
                title = {
                    Text(text = stringResource(R.string.string_share_add))
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = shareTitle,
                            onValueChange = {
                                shareTitle = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(text = stringResource(R.string.string_share_title))
                            },
                            maxLines = 1,
                        )
                        OutlinedTextField(
                            value = shareLink,
                            onValueChange = {
                                shareLink = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = {
                                Text(text = stringResource(R.string.string_share_link))
                            },
                            maxLines = 1,
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAddDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(text = stringResource(R.string.string_cancel))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val title = shareTitle.trim()
                            val link = shareLink.trim()
                            if (title.isBlank() || link.isBlank() || !isValidUrl(link)) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(addFailedMsg)
                                }
                            } else {
                                viewModel.addShareArticle(title = title, link = link)
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.string_confirm))
                    }
                },
            )
        }

        if (isSubmitting) {
            LoadingDialog(
                onDismissRequest = {
                    viewModel.cancelAddShare()
                }
            )
        }
    }
}

@Composable
private fun ShareItem(
    item: ShareArticleItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        val (authorRef, dateRef, imageRef, titleRef, descRef, chapterRef, deleteRef) = createRefs()
        Text(
            text = item.author.orEmpty().ifBlank { item.shareUser.orEmpty().ifBlank { "unknown" } },
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(authorRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = item.niceDate.orEmpty(),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(dateRef) {
                top.linkTo(authorRef.top)
                bottom.linkTo(authorRef.bottom)
                end.linkTo(parent.end)
            }
        )
        val authorBarrier = createBottomBarrier(authorRef, dateRef, margin = 10.dp)
        if (!item.envelopePic.isNullOrBlank()) {
            AsyncImage(
                model = item.envelopePic,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.constrainAs(imageRef) {
                    top.linkTo(authorBarrier)
                    start.linkTo(parent.start)
                    end.linkTo(titleRef.start, 8.dp)
                    width = Dimension.value(120.dp)
                    height = Dimension.value(90.dp)
                }
            )
        }
        Text(
            text = item.title.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(authorBarrier)
                if (!item.envelopePic.isNullOrBlank()) {
                    start.linkTo(imageRef.end, 8.dp)
                } else {
                    start.linkTo(parent.start)
                }
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = HtmlCompat.fromHtml(item.desc.orEmpty(), HtmlCompat.FROM_HTML_MODE_COMPACT)
                .toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(descRef) {
                top.linkTo(titleRef.bottom, 8.dp)
                start.linkTo(titleRef.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = "${item.superChapterName.orEmpty()} / ${item.chapterName.orEmpty()}",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.constrainAs(chapterRef) {
                top.linkTo(descRef.bottom, 8.dp)
                start.linkTo(titleRef.start)
                bottom.linkTo(parent.bottom)
                end.linkTo(deleteRef.start)
                width = Dimension.fillToConstraints
            }
        )
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.constrainAs(deleteRef) {
                top.linkTo(chapterRef.top)
                bottom.linkTo(chapterRef.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.string_share_delete),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun isValidUrl(url: String): Boolean {
    return url.startsWith("http://") || url.startsWith("https://")
}

@Preview(showBackground = true)
@Composable
fun ShareItemPreview(modifier: Modifier = Modifier) {
    ShareItem(
        item = ShareArticleItem(
            title = "title",
            author = "author",
            shareUser = "shareUser",
            niceDate = "2024-02-02",
            desc = "desc",
            chapterName = "chapterName",
            superChapterName = "superChapterName",
            envelopePic = "https://www.wanandroid.com/resources/image/pc/default_project_img.jpg"
        ),
        onClick = { },
        onDeleteClick = {},
        modifier = modifier
    )
}
