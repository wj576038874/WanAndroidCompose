package com.wanandroid.compose.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 阅读历史 ViewModel
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    /**
     * 阅读历史列表
     */
    val historyList = historyRepository.getHistoryList()
        .flow
        .cachedIn(viewModelScope)

    private val _historyEvent = MutableSharedFlow<HistoryEvent>()
    val historyEvent = _historyEvent.asSharedFlow()

    private val _deleteIdState = MutableStateFlow(0)
    val deleteIdState = _deleteIdState.asStateFlow()

    /**
     * 删除阅读历史
     */
    fun deleteHistory(id: Int) {
        viewModelScope.launch {
            historyRepository.deleteHistory(id).apply {
                onSuccess {
                    _deleteIdState.value = id
                    _historyEvent.emit(HistoryEvent.Success("删除成功"))
                }
                onFailure {
                    _historyEvent.emit(HistoryEvent.Error(it.message ?: "删除失败"))
                }
            }
        }
    }
}

/**
 * 阅读历史事件
 */
sealed class HistoryEvent {
    data class Success(val message: String) : HistoryEvent()
    data class Error(val message: String) : HistoryEvent()
}
