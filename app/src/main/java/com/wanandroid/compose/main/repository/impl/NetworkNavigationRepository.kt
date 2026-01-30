package com.wanandroid.compose.main.repository.impl

import com.wanandroid.compose.main.api.NavigationApi
import com.wanandroid.compose.main.repository.NavigationRepository
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/30.
 */
class NetworkNavigationRepository @Inject constructor(
    private val navigationApi: NavigationApi
) : NavigationRepository {

    /**
     * 获取导航列表
     */
    override suspend fun getNavigationList() = runCatching {
        val response = navigationApi.getNavigationList()
        if (response.isSuccess) {
            response.data ?: throw Exception("empty data")
        } else {
            throw Exception(response.message)
        }
    }
}