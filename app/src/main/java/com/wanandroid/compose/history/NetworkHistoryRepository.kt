package com.wanandroid.compose.history

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.ArticleItem
import jakarta.inject.Inject

/**
 * 阅读历史 Repository 实现
 */
class NetworkHistoryRepository @Inject constructor(
    private val historyApi: HistoryApi
) : HistoryRepository {

    override fun getHistoryList(): Pager<Int, ArticleItem> = Pager(
        pagingSourceFactory = { HistoryPagingSource(historyApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )

    override suspend fun deleteHistory(id: Int): Result<Any?> = runCatching {
        val response = historyApi.deleteHistory(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception("deleteHistory error ${response.message}")
        }
    }
}
