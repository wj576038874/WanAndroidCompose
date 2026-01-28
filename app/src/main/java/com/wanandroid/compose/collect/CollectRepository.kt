package com.wanandroid.compose.collect

import androidx.paging.Pager
import androidx.paging.PagingConfig

/**
 * Created by wenjie on 2026/01/28.
 */
class CollectRepository(private val collectApi: CollectApi) {

    fun getCollectList() = Pager(
        pagingSourceFactory = { CollectPagingSource(collectApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )

    suspend fun unCollectArticle(id: Int) = collectApi.unCollectArticle(id)
}