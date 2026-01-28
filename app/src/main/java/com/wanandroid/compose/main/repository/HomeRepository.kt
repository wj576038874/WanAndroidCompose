package com.wanandroid.compose.main.repository

import com.wanandroid.compose.main.api.HomeApi

/**
 * Created by wenjie on 2026/01/22.
 */
class HomeRepository(private val homeApi: HomeApi) {

    suspend fun getBannerList() = homeApi.getBannerList()

    suspend fun getArticleList(pageNum: Int) = homeApi.getArticleList(pageNum)

    suspend fun collectArticle(id: Int) = homeApi.collectArticle(id)

     suspend fun unCollectArticle(id: Int) = homeApi.unCollectArticle(id)
}