package com.wanandroid.compose.main.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.WanAndroidApplication
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.main.event.NavigationEvent
import com.wanandroid.compose.main.viemodel.NavigationViewModel
import com.wanandroid.compose.utils.ObserveAsEvents
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
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    val onArticleClick = remember(context, toolbarColor) {
        { articleItem: ArticleItem ->
            launchCustomChromeTab(
                context = context,
                uri = articleItem.link.toUri(),
                toolbarColor = toolbarColor
            )
        }
    }

    val navigationUiState by viewModel.navigationUiState.collectAsStateWithLifecycle()

    val itemList = navigationUiState.navigationList

    ObserveAsEvents(
        flow = viewModel.navigationEvent,
        onEvent = { event ->
            when (event) {
                is NavigationEvent.NavigationError -> {
                    Toast.makeText(
                        WanAndroidApplication.context,
                        event.errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )

    Column(
        modifier = modifier.fillMaxSize(),
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
            BoxWithConstraints(
                modifier = modifier.fillMaxSize()
            ) {
                val density = LocalDensity.current
                val textMeasurer = rememberTextMeasurer()
                val chipLabelStyle = MaterialTheme.typography.labelLarge
                val sectionHorizontalPadding = 16.dp
                val chipHorizontalSpace = 8.dp
                val availableWidthPx = with(density) {
                    (maxWidth - sectionHorizontalPadding * 2).coerceAtLeast(0.dp).toPx()
                }
                val chipHorizontalSpacePx = with(density) { chipHorizontalSpace.toPx() }
                val chipExtraWidthPx = with(density) { 40.dp.toPx() }

                val articleRowsByCid = remember(
                    itemList,
                    availableWidthPx,
                    chipHorizontalSpacePx,
                    chipExtraWidthPx,
                    chipLabelStyle
                ) {
                    itemList.associate { navigationItem ->
                        val rows = buildFlowRows(
                            articles = navigationItem.articles,
                            availableWidthPx = availableWidthPx,
                            horizontalSpacePx = chipHorizontalSpacePx,
                            measureChipWidthPx = { article ->
                                measureChipWidthPx(
                                    title = article.title,
                                    textMeasurer = textMeasurer,
                                    textStyle = chipLabelStyle,
                                    chipExtraWidthPx = chipExtraWidthPx,
                                    maxWidthPx = availableWidthPx,
                                )
                            }
                        )
                        navigationItem.cid to rows
                    }
                }
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                ) {
                    itemList.forEach { navigationItem ->
                        stickyHeader(
                            key = "header_${navigationItem.cid}"
                        ) {
                            NavigationHeader(
                                title = navigationItem.name
                            )
                        }
                        val rows = articleRowsByCid[navigationItem.cid].orEmpty()
                        rows.forEachIndexed { rowIndex, rowArticles ->
                            item(
                                key = "row_${navigationItem.cid}_$rowIndex"
                            ) {
                                NavigationItemRow(
                                    articles = rowArticles,
                                    isFirstRow = rowIndex == 0,
                                    isLastRow = rowIndex == rows.lastIndex,
                                    onArticleClick = onArticleClick
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
fun NavigationItemRow(
    modifier: Modifier = Modifier,
    articles: List<ArticleItem>,
    isFirstRow: Boolean,
    isLastRow: Boolean,
    onArticleClick: (ArticleItem) -> Unit,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = if (isFirstRow) 16.dp else 0.dp,
                bottom = if (isLastRow) 16.dp else 0.dp,
            ),
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
                    onArticleClick(articleItem)
                },
                label = {
                    Text(
                        text = articleItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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

private fun measureChipWidthPx(
    title: String,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    textStyle: TextStyle,
    chipExtraWidthPx: Float,
    maxWidthPx: Float,
): Float {
    if (maxWidthPx <= 0f) return 0f
    val textWidth = textMeasurer.measure(
        text = AnnotatedString(title),
        style = textStyle,
        maxLines = 1,
    ).size.width.toFloat()
    return (textWidth + chipExtraWidthPx).coerceAtMost(maxWidthPx)
}

private fun buildFlowRows(
    articles: List<ArticleItem>,
    availableWidthPx: Float,
    horizontalSpacePx: Float,
    measureChipWidthPx: (ArticleItem) -> Float,
): List<List<ArticleItem>> {
    if (articles.isEmpty()) return emptyList()
    if (availableWidthPx <= 0f) return articles.map { listOf(it) }

    val rows = mutableListOf<MutableList<ArticleItem>>()
    var currentRow = mutableListOf<ArticleItem>()
    var currentWidth = 0f

    articles.forEach { article ->
        val chipWidth = measureChipWidthPx(article).coerceAtLeast(0f)
        val requiredWidth = if (currentRow.isEmpty()) chipWidth else chipWidth + horizontalSpacePx

        if (currentRow.isNotEmpty() && currentWidth + requiredWidth > availableWidthPx) {
            rows += currentRow
            currentRow = mutableListOf(article)
            currentWidth = chipWidth
        } else {
            currentRow += article
            currentWidth += requiredWidth
        }
    }

    if (currentRow.isNotEmpty()) {
        rows += currentRow
    }
    return rows
}
