package com.wanandroid.compose.main.repository

import com.wanandroid.compose.http.ApiService

/**
 * Created by wenjie on 2026/01/22.
 */
class HomeRepository(private val apiService: ApiService) {

    suspend fun getBannerList() = apiService.getBannerList()

    suspend fun getArticleList(pageNum: Int) = apiService.getArticleList(pageNum)

    suspend fun collectArticle(id: Int) = apiService.collectArticle(id)

     suspend fun unCollectArticle(id: Int) = apiService.unCollectArticle(id)
}