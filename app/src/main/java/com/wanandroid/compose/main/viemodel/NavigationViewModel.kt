package com.wanandroid.compose.main.viemodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.main.repository.NavigationRepository
import com.wanandroid.compose.main.state.NavigationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/23.
 */
class NavigationViewModel(
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
            runCatching {
                _navigationUiState.value = _navigationUiState.value.copy(
                    isLoading = true
                )
                val response = navigationRepository.getNavigationList()
                if (response.isSuccess) {
                    response.data
                } else {
                    throw Exception(response.message)
                }
            }.onSuccess { navigationItems ->
                _navigationUiState.value = _navigationUiState.value.copy(
                    isLoading = false, navigationList = navigationItems ?: emptyList()
                )
            }.onFailure {
                _navigationUiState.value = _navigationUiState.value.copy(
                    errorMessage = it.message ?: "获取导航列表失败", isLoading = false
                )
            }
        }
    }
}