package com.wanandroid.compose.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.ArticleItem
import jakarta.inject.Inject

/**
 * 搜索 Repository 实现
 */
class NetworkSearchRepository @Inject constructor(
    private val searchApi: SearchApi
) : SearchRepository {

    override fun searchArticles(keyword: String): Pager<Int, ArticleItem> = Pager(
        pagingSourceFactory = { SearchPagingSource(searchApi, keyword) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )
}
