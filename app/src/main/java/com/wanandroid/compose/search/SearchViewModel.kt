package com.wanandroid.compose.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.search.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()
    private val searchRequest = MutableStateFlow(SearchRequest())

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<ArticleItem>> = searchRequest
        .flatMapLatest { request ->
            if (request.keyword.isBlank()) flowOf(PagingData.empty())
            else searchRepository.searchArticles(request.keyword).flow
        }
        .cachedIn(viewModelScope)

    init {
        loadHotKeywords()
    }

    fun onSearchQueryChange(query: String) {
        if (query.isBlank()) {
            clearSearch()
        } else {
            _uiState.update { it.copy(query = query) }
        }
    }

    fun search(keyword: String = _uiState.value.query) {
        val normalized = keyword.trim()
        if (normalized.isEmpty()) {
            clearSearch()
            return
        }
        _uiState.update { it.copy(query = normalized, submittedQuery = normalized) }
        // A new request also permits retrying the same keyword after a failure.
        searchRequest.update { SearchRequest(normalized, it.generation + 1) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(query = "", submittedQuery = "") }
        searchRequest.update { SearchRequest(generation = it.generation + 1) }
    }

    fun loadHotKeywords() {
        if (_uiState.value.isHotKeywordsLoading) return
        _uiState.update { it.copy(isHotKeywordsLoading = true, hotKeywordsFailed = false) }
        viewModelScope.launch {
            searchRepository.getHotSearchKeywords().fold(
                onSuccess = { keywords ->
                    _uiState.update {
                        it.copy(hotKeywords = keywords, isHotKeywordsLoading = false, hotKeywordsFailed = false)
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isHotKeywordsLoading = false, hotKeywordsFailed = true) }
                },
            )
        }
    }

    private data class SearchRequest(val keyword: String = "", val generation: Long = 0)
}
