package com.wanandroid.compose.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.search.state.SearchUiState
import com.wanandroid.compose.utils.launchCustomChromeTab

@Composable
fun SearchScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val results = viewModel.searchResults.collectAsLazyPagingItems()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    SearchContent(
        state = state,
        results = results,
        onQueryChange = viewModel::onSearchQueryChange,
        onSearch = viewModel::search,
        onClear = viewModel::clearSearch,
        onRetryHotKeywords = viewModel::loadHotKeywords,
        onBackClick = onBackClick,
        onArticleClick = { launchCustomChromeTab(context, it.link.toUri(), toolbarColor) },
        modifier = modifier,
    )
}

@Composable
internal fun SearchContent(
    state: SearchUiState,
    results: LazyPagingItems<ArticleItem>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onRetryHotKeywords: () -> Unit,
    onBackClick: () -> Unit,
    onArticleClick: (ArticleItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val submit: (String) -> Unit = { keyword ->
        onSearch(keyword)
        focusManager.clearFocus()
        keyboard?.hide()
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(color = MaterialTheme.colorScheme.primary) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.string_back))
                    }
                    SearchTextField(
                        query = state.query,
                        onQueryChange = onQueryChange,
                        onSearch = { submit(state.query) },
                        onClear = onClear,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { submit(state.query) }, enabled = state.query.isNotBlank()) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.string_search_submit))
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding).imePadding(),
        ) {
            if (state.submittedQuery.isBlank()) {
                PopularSearches(state, submit, onRetryHotKeywords)
            } else {
                SearchResults(results, onArticleClick)
            }
        }
    }
}

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth().heightIn(min = TextFieldDefaults.MinHeight),
        placeholder = { Text(stringResource(R.string.string_search_hint), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.string_clear))
                }
            }
        } else null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun PopularSearches(state: SearchUiState, onSearch: (String) -> Unit, onRetry: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Text(stringResource(R.string.string_search_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.string_search_hot), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            when {
                state.isHotKeywordsLoading -> Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center).size(24.dp))
                }
                state.hotKeywordsFailed -> SearchError(stringResource(R.string.string_search_hot_failed), onRetry)
                state.hotKeywords.isEmpty() -> Text(stringResource(R.string.string_search_hot_empty))
                else -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.hotKeywords.forEach { keyword ->
                        AssistChip(
                            onClick = { onSearch(keyword.name) },
                            label = { Text(keyword.name) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResults(results: LazyPagingItems<ArticleItem>, onArticleClick: (ArticleItem) -> Unit) {
    val refresh = results.loadState.refresh
    PullToRefreshBox(isRefreshing = refresh is LoadState.Loading, onRefresh = results::refresh) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
            if (refresh is LoadState.Error) {
                item(key = "refresh_error", contentType = "status") {
                    SearchError(stringResource(R.string.string_search_failed), results::retry)
                }
            } else if (refresh is LoadState.NotLoading && results.itemCount == 0) {
                item(key = "empty", contentType = "status") {
                    Text(stringResource(R.string.string_search_no_result), Modifier.padding(24.dp))
                }
            }
            items(count = results.itemCount, key = results.itemKey { it.id }, contentType = { "article" }) { index ->
                results[index]?.let { item ->
                    SearchResultItem(item, onClick = { onArticleClick(item) })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
            when (results.loadState.append) {
                is LoadState.Loading -> item(key = "append_loading", contentType = "status") {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                        Text(stringResource(R.string.string_search_loading), Modifier.padding(start = 8.dp))
                    }
                }
                is LoadState.Error -> item(key = "append_error", contentType = "status") {
                    SearchError(stringResource(R.string.string_search_failed), results::retry)
                }
                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun SearchError(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onRetry) { Text(stringResource(R.string.string_search_retry)) }
    }
}

/**
 * 搜索结果项
 */
@Composable
fun SearchResultItem(
    articleItem: ArticleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 标题
        Text(
            text = remember(articleItem.title) {
                HtmlCompat.fromHtml(articleItem.title, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 作者和时间
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = articleItem.author.ifBlank { articleItem.shareUser.ifBlank { stringResource(R.string.string_search_anonymous) } },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = articleItem.niceDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // 分类标签
        if (articleItem.superChapterName.isNotBlank() || articleItem.chapterName.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))

            val category = buildString {
                if (articleItem.superChapterName.isNotBlank()) {
                    append(articleItem.superChapterName)
                }
                if (articleItem.chapterName.isNotBlank()) {
                    if (isNotEmpty()) append(" / ")
                    append(articleItem.chapterName)
                }
            }

            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
