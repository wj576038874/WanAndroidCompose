package com.wanandroid.compose.message

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.MessageItem

/**
 * Created by wenjie on 2026/04/20.
 */
class MessagePagingSource(
    private val loadPage: suspend (Int) -> MessagePage
) : PagingSource<Int, MessageItem>() {

    override fun getRefreshKey(state: PagingState<Int, MessageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageItem> {
        return try {
            val page = params.key ?: 1
            val result = loadPage(page)
            LoadResult.Page(
                data = result.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.over) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

data class MessagePage(
    val items: List<MessageItem>,
    val over: Boolean,
)
