package com.wanandroid.compose.main.state

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BannerItem

/**
 * Created by wenjie on 2026/01/22.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val bannerList: List<BannerItem>? = null,
    val articleList: List<ArticleItem>? = null,
    val noMoreData: Boolean = false,
    val nextPageError: String? = null
)