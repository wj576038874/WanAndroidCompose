package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.main.event.NavigationEvent
import com.wanandroid.compose.main.repository.NavigationRepository
import com.wanandroid.compose.main.state.NavigationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/23.
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationRepository: NavigationRepository
) : ViewModel() {

    private val _navigationUiState = MutableStateFlow(NavigationUiState())
    val navigationUiState = _navigationUiState.asStateFlow()

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        getNavigationList()
    }

    /**
     * 获取导航列表
     */
    fun getNavigationList() {
        viewModelScope.launch {
            _navigationUiState.update { state ->
                state.copy(
                    isLoading = true
                )
            }
            navigationRepository.getNavigationList().apply {
                onSuccess {
                    _navigationUiState.update { state ->
                        state.copy(
                            isLoading = false, navigationList = it
                        )
                    }
                }
                onFailure {
                    _navigationEvent.send(
                        NavigationEvent.NavigationError(
                            it.message ?: "获取导航列表失败"
                        )
                    )
                }
            }
        }
    }
}