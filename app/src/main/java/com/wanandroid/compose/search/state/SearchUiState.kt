package com.wanandroid.compose.search.state

import com.wanandroid.compose.bean.HotSearchKeyword

data class SearchUiState(
    val query: String = "",
    val submittedQuery: String = "",
    val hotKeywords: List<HotSearchKeyword> = emptyList(),
    val isHotKeywordsLoading: Boolean = false,
    val hotKeywordsFailed: Boolean = false,
)
