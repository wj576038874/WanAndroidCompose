package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.main.repository.QuestionAnswerRepository

/**
 * Created by wenjie on 2026/01/23.
 */
class QuestionAnswerViewModel(questionAnswerRepository: QuestionAnswerRepository) :
    ViewModel() {

    val questionAnswerList = questionAnswerRepository.getQuestionAnswerList()
        .cachedIn(viewModelScope)

}