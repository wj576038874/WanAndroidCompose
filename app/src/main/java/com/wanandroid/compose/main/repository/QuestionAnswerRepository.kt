package com.wanandroid.compose.main.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.compose.bean.QuestionAnswerItem
import com.wanandroid.compose.http.QuestionAnswerApi
import com.wanandroid.compose.main.paging.QuestionAnswerPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by wenjie on 2026/01/23.
 */
class QuestionAnswerRepository(private val questionAnswerApi: QuestionAnswerApi) {

    fun getQuestionAnswerList(): Flow<PagingData<QuestionAnswerItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
                prefetchDistance = 1,
            ), pagingSourceFactory = {
                QuestionAnswerPagingSource(questionAnswerApi)
            }).flow
    }
}