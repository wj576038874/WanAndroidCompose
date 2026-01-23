package com.wanandroid.compose.main.repository

import com.wanandroid.compose.http.NavigationApi

/**
 * Created by wenjie on 2026/01/23.
 */
class NavigationRepository(
    private val navigationApi: NavigationApi
) {
    /**
     * 获取导航列表
     */
    suspend fun getNavigationList() = navigationApi.getNavigationList()
}