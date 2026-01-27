package com.wanandroid.compose.coin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.CoinItem
import com.wanandroid.compose.http.CoinApi

/**
 * Created by wenjie on 2026/01/27.
 */
class CoinPagingSource(private val coinApi: CoinApi) : PagingSource<Int, CoinItem>() {

    override fun getRefreshKey(state: PagingState<Int, CoinItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                anchor
            )?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val response = coinApi.getCoinList(page)
            if (response.code == 0) {
                val data = response.data ?: return LoadResult.Error(Exception("No data field"))
                val items = data.datas ?: return LoadResult.Error(Exception("No datas field"))
                val nextKey = if (data.over) {
                    null
                } else {
                    page + 1
                }
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception(response.message))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}