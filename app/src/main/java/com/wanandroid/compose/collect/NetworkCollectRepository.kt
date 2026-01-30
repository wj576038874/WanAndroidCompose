package com.wanandroid.compose.collect

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.ArticleItem
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/30.
 */
class NetworkCollectRepository @Inject constructor(
    private val collectApi: CollectApi
) : CollectRepository {

    override fun getCollectList(): Pager<Int, ArticleItem> = Pager(
        pagingSourceFactory = { CollectPagingSource(collectApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )

    override suspend fun unCollectArticle(id: Int) = runCatching {
        val response = collectApi.unCollectArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception("unCollectArticle error ${response.message}")
        }
    }
}