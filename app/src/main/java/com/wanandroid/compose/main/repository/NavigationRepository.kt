package com.wanandroid.compose.main.repository

import com.wanandroid.compose.bean.NavigationItem

/**
 * Created by wenjie on 2026/01/23.
 */
interface NavigationRepository{
    /**
     * 获取导航列表
     */
    suspend fun getNavigationList(): Result<List<NavigationItem>>
}