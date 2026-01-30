package com.wanandroid.compose.main.repository

import androidx.paging.PagingData
import com.wanandroid.compose.bean.QuestionAnswerItem
import kotlinx.coroutines.flow.Flow

/**
 * Created by wenjie on 2026/01/23.
 */
interface QuestionAnswerRepository {

    fun getQuestionAnswerList(): Flow<PagingData<QuestionAnswerItem>>

    suspend fun collectArticle(id: Int): Result<Any?>

    suspend fun unCollectArticle(id: Int): Result<Any?>
}