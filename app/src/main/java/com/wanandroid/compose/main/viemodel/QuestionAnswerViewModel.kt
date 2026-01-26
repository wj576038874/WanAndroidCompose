package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.main.repository.QuestionAnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/23.
 */
class QuestionAnswerViewModel(private val questionAnswerRepository: QuestionAnswerRepository) :
    ViewModel() {

    val questionAnswerList = questionAnswerRepository.getQuestionAnswerList()
//        .map { pagingData ->
//            pagingData.map {
//                it.copy(
//                    collect = collectIds.value.contains(it.id)
//                )
//            }
//        }
        .cachedIn(viewModelScope)

    private val _collectedIds: MutableStateFlow<Collection<Int>> = MutableStateFlow(
        UserManager.instance.userInfo.value?.collectIds ?: emptySet()
    )
    val collectedIds = _collectedIds.asStateFlow()

    fun collectArticle(id: Int) {
        viewModelScope.launch {
            runCatching {
                questionAnswerRepository.collectArticle(id)
            }.onSuccess { response ->
                if (response.isSuccess) {
                    _collectedIds.value += id
                }
            }.onFailure {

            }
        }
    }

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            runCatching {
                questionAnswerRepository.unCollectArticle(id)
            }.onSuccess {
                _collectedIds.value -= id
            }.onFailure {

            }
        }
    }
}