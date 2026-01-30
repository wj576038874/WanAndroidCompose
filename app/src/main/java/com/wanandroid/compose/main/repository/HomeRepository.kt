package com.wanandroid.compose.main.repository

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.main.api.HomeApi
import javax.inject.Inject

/**
 * Created by wenjie on 2026/01/22.
 */
class HomeRepository @Inject constructor(private val homeApi: HomeApi) : IHomeRepository {

    override suspend fun getBannerList() = runCatching {
        val response = homeApi.getBannerList()
        if (response.isSuccess) {
            response.data ?: throw Exception("empty data")
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun getArticleList(pageNum: Int) = runCatching {
        val response = homeApi.getArticleList(pageNum)
        if (response.isSuccess) {
            response.data ?: throw Exception("empty data")
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun collectArticle(id: Int) = runCatching {
        val response = homeApi.collectArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message)
        }
    }

    override suspend fun unCollectArticle(id: Int) = runCatching {
        val response = homeApi.unCollectArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message)
        }
    }
}