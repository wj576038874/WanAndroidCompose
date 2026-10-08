package com.wanandroid.compose.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.ArticleItem
import kotlinx.coroutines.CancellationException

/**
 * 搜索分页数据源
 */
class SearchPagingSource(
    private val searchApi: SearchApi,
    private val keyword: String
) : PagingSource<Int, ArticleItem>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> {
        return try {
            val page = params.key ?: FIRST_PAGE

            val response = searchApi.searchArticles(page, keyword)
            if (!response.isSuccess) return LoadResult.Error(IllegalStateException(response.message))
            val pageData = requireNotNull(response.data)
            val articles = requireNotNull(pageData.datas)

            val nextKey = if (pageData.over || articles.isEmpty()) {
                null
            } else {
                page + 1
            }
            val prevKey = if (page == FIRST_PAGE) {
                null
            } else {
                page - 1
            }
            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val FIRST_PAGE = 0
    }
}
