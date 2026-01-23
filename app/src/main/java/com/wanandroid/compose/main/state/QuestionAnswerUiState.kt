package com.wanandroid.compose.main.state

import com.wanandroid.compose.bean.QuestionAnswerItem

/**
 * Created by wenjie on 2026/01/23.
 */
data class QuestionAnswerUiState(
    val questionAnswerList: List<QuestionAnswerItem>? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val noMorData: Boolean = false,
)