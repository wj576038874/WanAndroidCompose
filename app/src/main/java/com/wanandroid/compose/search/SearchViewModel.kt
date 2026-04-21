package com.wanandroid.compose.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.compose.bean.ArticleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * 搜索 ViewModel
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * 搜索关键词流，用于触发搜索
     */
    private val _currentKeyword = MutableStateFlow<String?>(null)

    /**
     * 搜索结果流
     * 使用 flatMapLatest 实现当关键词变化时自动切换数据源
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<ArticleItem>> = _currentKeyword
        .flatMapLatest { keyword ->
            if (keyword.isNullOrBlank()) {
                flowOf(PagingData.empty())
            } else {
                searchRepository.searchArticles(keyword).flow
            }
        }
        .cachedIn(viewModelScope)

    /**
     * 更新搜索输入框的内容
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * 执行搜索
     */
    fun search(keyword: String = _searchQuery.value) {
        if (keyword.isNotBlank()) {
            _currentKeyword.value = keyword
        }
    }

    /**
     * 清除搜索
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _currentKeyword.value = null
    }
}
