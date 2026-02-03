package com.wanandroid.compose.main.state

import com.wanandroid.compose.bean.NavigationItem

/**
 * Created by wenjie on 2026/01/23.
 */
data class NavigationUiState(
    val navigationList: List<NavigationItem> = emptyList(),
    val isLoading: Boolean = false,
)
