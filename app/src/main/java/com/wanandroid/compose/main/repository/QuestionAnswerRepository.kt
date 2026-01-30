package com.wanandroid.compose.main.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.compose.bean.QuestionAnswerItem
import com.wanandroid.compose.main.api.QuestionAnswerApi
import com.wanandroid.compose.main.paging.QuestionAnswerPagingSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Created by wenjie on 2026/01/23.
 */
class QuestionAnswerRepository @Inject constructor(private val questionAnswerApi: QuestionAnswerApi) {

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

    suspend fun collectArticle(id: Int) = runCatching {
        val response = questionAnswerApi.collectArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message)
        }
    }

    suspend fun unCollectArticle(id: Int) = runCatching {
        val response = questionAnswerApi.unCollectArticle(id)
        if (response.isSuccess) {
            response.data
        } else {
            throw Exception(response.message)
        }
    }
}