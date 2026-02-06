package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.collect.event.CollectEvent
import com.wanandroid.compose.main.repository.QuestionAnswerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/23.
 */
@HiltViewModel
class QuestionAnswerViewModel @Inject constructor(private val questionAnswerRepository: QuestionAnswerRepository) :
    ViewModel() {

    val questionAnswerList = questionAnswerRepository.getQuestionAnswerList()
        .cachedIn(viewModelScope)

    val collectedIds = UserManager.instance.userInfo.map {
        it?.collectIds ?: emptySet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserManager.instance.userInfo.value?.collectIds ?: emptySet()
    )

    private val _collectEvent = MutableSharedFlow<CollectEvent>()
    val collectEvent = _collectEvent.asSharedFlow()

    fun collectArticle(id: Int) {
        viewModelScope.launch {
            questionAnswerRepository.collectArticle(id).apply {
                onSuccess {
                    UserManager.instance.addCollectId(id)
                }
                onFailure {
                    _collectEvent.emit(
                        CollectEvent(it.message ?: "Collect failed")
                    )
                }
            }
        }
    }

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            questionAnswerRepository.unCollectArticle(id).apply {
                onSuccess {
                    UserManager.instance.removeCollectId(id)
                }
                onFailure {
                    _collectEvent.emit(
                        CollectEvent(it.message ?: "Un collect failed")
                    )
                }
            }
        }
    }
}