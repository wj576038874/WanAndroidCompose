package com.wanandroid.compose.main.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.QuestionAnswerItem
import com.wanandroid.compose.http.QuestionAnswerApi

/**
 * Created by wenjie on 2026/01/23.
 */
class QuestionAnswerPagingSource(
    private val questionAnswerApi: QuestionAnswerApi
) : PagingSource<Int, QuestionAnswerItem>() {

    override fun getRefreshKey(state: PagingState<Int, QuestionAnswerItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                anchor
            )?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestionAnswerItem> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            Log.e("asd", "page = $page, pageSize = $pageSize")
            val response = questionAnswerApi.getQuestionAnswerList(pageNum = page)
            if (response.code == 0) {
                val data = response.data ?: return LoadResult.Error(Exception("No data field"))
                val items = data.datas ?: return LoadResult.Error(Exception("No datas field"))
                // 关键：使用 over 来决定是否还有下一页
                val nextKey = if (page == 2) {
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