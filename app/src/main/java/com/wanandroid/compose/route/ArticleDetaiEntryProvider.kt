package com.wanandroid.compose.route

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.main.screen.ArticleDetailScreen

/**
 * Created by wenjie on 2026/02/02.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.forArticleDetailScreen(navigator: Navigator) {
    entry<RouteNavKey.ArticleDetail> {
        ArticleDetailScreen(
            onBackClick = {
                navigator.goBack()
            },
            articleItem = it.articleItem
        )
    }
}
