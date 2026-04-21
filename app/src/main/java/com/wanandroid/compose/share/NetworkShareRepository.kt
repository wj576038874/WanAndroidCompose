package com.wanandroid.compose.share

import androidx.paging.Pager
import androidx.paging.PagingConfig
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/04/21.
 */
class NetworkShareRepository @Inject constructor(
    private val shareApi: ShareApi
) : ShareRepository {

    override fun getShareArticleList() = Pager(
        pagingSourceFactory = { SharePagingSource(shareApi = shareApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )

    override suspend fun addShareArticle(title: String, link: String) = runCatching {
        val response = shareApi.addShareArticle(title = title, link = link)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message ?: "Share article failed")
        }
    }

    override suspend fun deleteShareArticle(id: Int) = runCatching {
        val response = shareApi.deleteShareArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message ?: "Delete article failed")
        }
    }
}
