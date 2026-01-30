package com.wanandroid.compose.main.repository

import com.wanandroid.compose.main.api.NavigationApi
import javax.inject.Inject

/**
 * Created by wenjie on 2026/01/23.
 */
class NavigationRepository @Inject constructor(
    private val navigationApi: NavigationApi
) {
    /**
     * 获取导航列表
     */
    suspend fun getNavigationList() = runCatching {
        val response = navigationApi.getNavigationList()
        if (response.isSuccess) {
            response.data ?: throw Exception("empty data")
        } else {
            throw Exception(response.message)
        }
    }
}