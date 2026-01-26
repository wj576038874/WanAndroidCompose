package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.main.repository.QuestionAnswerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val collectedIds: StateFlow<Set<Int>> = UserManager.instance.userInfo.map {
        it?.collectIds ?: emptySet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserManager.instance.userInfo.value?.collectIds ?: emptySet()
    )

    fun collectArticle(id: Int) {
        viewModelScope.launch {
            runCatching {
                questionAnswerRepository.collectArticle(id)
            }.onSuccess { response ->
                if (response.isSuccess) {
                    UserManager.instance.addCollectId(id)
                }
            }.onFailure {

            }
        }
    }

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            runCatching {
                questionAnswerRepository.unCollectArticle(id)
            }.onSuccess { response ->
                if (response.isSuccess) {
                    UserManager.instance.removeCollectId(id)
                }
            }.onFailure {

            }
        }
    }
}