package com.wanandroid.compose.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/04/20.
 */
@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    val unreadMessageList = messageRepository.getUnreadMessageList()
        .flow
        .cachedIn(viewModelScope)

    val readMessageList = messageRepository.getReadMessageList()
        .flow
        .cachedIn(viewModelScope)

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _isLoadingCount = MutableStateFlow(false)
    val isLoadingCount = _isLoadingCount.asStateFlow()

    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent = _messageEvent.asSharedFlow()

    init {
        refreshUnreadCount()
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            _isLoadingCount.update { true }
            messageRepository.getUnreadCount()
                .onSuccess { count ->
                    _unreadCount.update { count }
                }
                .onFailure {
                    _messageEvent.emit(it.message ?: "Load unread count failed")
                }
            _isLoadingCount.update { false }
        }
    }
}
