package com.wanandroid.compose.bookmark.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.action.BookMarkAction
import com.wanandroid.compose.bookmark.state.BookMarkUiState
import com.wanandroid.compose.common.CommonToolbar

@Composable
internal fun BookMarkContent(
    state: BookMarkUiState,
    onAction: (BookMarkAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_book_mark),
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { onAction(BookMarkAction.Add) }, enabled = !state.isBusy) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.string_bookmark_add),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onAction(BookMarkAction.Refresh) },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                if (state.loadFailed) {
                    item(key = "load_error", contentType = "load_error") {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = state.loadError ?: stringResource(R.string.string_bookmark_load_failed),
                                color = MaterialTheme.colorScheme.error,
                            )
                            TextButton(onClick = { onAction(BookMarkAction.Refresh) }) {
                                Text(stringResource(R.string.string_bookmark_retry))
                            }
                        }
                    }
                }
                if (state.items.isEmpty() && !state.isLoading && !state.loadFailed) {
                    item(key = "empty", contentType = "empty") {
                        Text(
                            text = stringResource(R.string.string_bookmark_empty),
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
                items(state.items, key = { it.id }, contentType = { "bookmark" }) { item ->
                    BookMarkRow(item, enabled = !state.isBusy, onAction = onAction)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (state.showEditor) BookMarkEditor(state, onAction)
    state.deleteTarget?.let { item ->
        AlertDialog(
            onDismissRequest = { onAction(BookMarkAction.DismissDelete) },
            title = { Text(stringResource(R.string.string_delete)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.string_bookmark_delete_confirm, item.name))
                    if (state.mutationFailed) {
                        Text(
                            state.mutationError ?: stringResource(R.string.string_bookmark_delete_failed),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onAction(BookMarkAction.ConfirmDelete) }, enabled = !state.isBusy) {
                    Text(
                        stringResource(if (state.isSubmitting) R.string.string_bookmark_deleting else R.string.string_confirm),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(BookMarkAction.DismissDelete) }, enabled = !state.isSubmitting) {
                    Text(stringResource(R.string.string_cancel))
                }
            },
        )
    }
}

@Composable
private fun BookMarkRow(item: BookMarkItem, enabled: Boolean, onAction: (BookMarkAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(BookMarkAction.Open(item)) }
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                item.link,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = { onAction(BookMarkAction.Edit(item)) }, enabled = enabled) {
            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.string_bookmark_edit))
        }
        IconButton(onClick = { onAction(BookMarkAction.RequestDelete(item)) }, enabled = enabled) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = stringResource(R.string.string_delete))
        }
    }
}

@Composable
private fun BookMarkEditor(state: BookMarkUiState, onAction: (BookMarkAction) -> Unit) {
    AlertDialog(
        onDismissRequest = { onAction(BookMarkAction.DismissEditor) },
        title = {
            Text(stringResource(if (state.editingId == null) R.string.string_bookmark_add else R.string.string_bookmark_edit))
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(BookMarkAction.NameChanged(it)) },
                    label = { Text(stringResource(R.string.string_bookmark_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSubmitting,
                    singleLine = true,
                    isError = state.nameInvalid,
                    supportingText = if (state.nameInvalid) {
                        { Text(stringResource(R.string.string_bookmark_name_invalid)) }
                    } else null,
                )
                OutlinedTextField(
                    value = state.link,
                    onValueChange = { onAction(BookMarkAction.LinkChanged(it)) },
                    label = { Text(stringResource(R.string.string_bookmark_link)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSubmitting,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    isError = state.linkInvalid,
                    supportingText = if (state.linkInvalid) {
                        { Text(stringResource(R.string.string_bookmark_link_invalid)) }
                    } else null,
                )
                if (state.mutationFailed) {
                    Text(
                        state.mutationError ?: stringResource(R.string.string_bookmark_save_failed),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onAction(BookMarkAction.Save) }, enabled = !state.isBusy) {
                Text(stringResource(if (state.isSubmitting) R.string.string_bookmark_saving else R.string.string_bookmark_save))
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(BookMarkAction.DismissEditor) }, enabled = !state.isSubmitting) {
                Text(stringResource(R.string.string_cancel))
            }
        },
    )
}
