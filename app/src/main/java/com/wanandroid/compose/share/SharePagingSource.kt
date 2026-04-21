package com.wanandroid.compose.share

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Created by wenjie on 2026/04/21.
 */
class SharePagingSource(
    private val shareApi: ShareApi
) : PagingSource<Int, ShareArticleItem>() {

    override fun getRefreshKey(state: PagingState<Int, ShareArticleItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShareArticleItem> {
        return try {
            val page = params.key ?: 1
            val response = shareApi.getShareArticleList(page)
            if (!response.isSuccess) {
                return LoadResult.Error(Exception(response.message))
            }
            val pageData = response.data?.shareArticles ?: return LoadResult.Error(Exception("data is null"))
            val articles = pageData.datas ?: emptyList()
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (pageData.over) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
