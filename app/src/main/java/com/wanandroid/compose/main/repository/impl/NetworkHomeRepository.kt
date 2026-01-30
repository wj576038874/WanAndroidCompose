package com.wanandroid.compose.main.repository.impl

import com.wanandroid.compose.main.api.HomeApi
import com.wanandroid.compose.main.repository.HomeRepository
import javax.inject.Inject

/**
 * Created by wenjie on 2026/01/22.
 */
class NetworkHomeRepository @Inject constructor(private val homeApi: HomeApi) : HomeRepository {

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