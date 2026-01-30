package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.main.repository.NavigationRepository
import com.wanandroid.compose.main.state.NavigationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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


    init {
        getNavigationList()
    }

    /**
     * 获取导航列表
     */
    fun getNavigationList() {
        viewModelScope.launch {
            _navigationUiState.value = _navigationUiState.value.copy(
                isLoading = true
            )
            navigationRepository.getNavigationList().apply {
                onSuccess {
                    _navigationUiState.value = _navigationUiState.value.copy(
                        isLoading = false, navigationList = it
                    )
                }
                onFailure {
                    _navigationUiState.value = _navigationUiState.value.copy(
                        errorMessage = it.message ?: "获取导航列表失败", isLoading = false
                    )
                }
            }
        }
    }
}