package com.wanandroid.compose.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.ArticleItem

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
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val response = searchApi.searchArticles(page, keyword)
            val pageData = response.data ?: return LoadResult.Error(Exception("data is null"))
            val articles = pageData.datas ?: return LoadResult.Error(Exception("datas is null"))

            val nextKey = if (pageData.over) {
                null
            } else {
                page + 1
            }
            val prevKey = if (page == 0) {
                null
            } else {
                page - 1
            }
            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
