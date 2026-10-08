package com.wanandroid.compose.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.HotSearchKeyword
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * 搜索 Repository 实现
 */
class NetworkSearchRepository @Inject constructor(
    private val searchApi: SearchApi
) : SearchRepository {

    override suspend fun getHotSearchKeywords(): Result<List<HotSearchKeyword>> = try {
        val response = searchApi.getHotSearchKeywords()
        if (!response.isSuccess) throw IllegalStateException(response.message)
        Result.success(requireNotNull(response.data))
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (exception: Exception) {
        Result.failure(exception)
    }

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
