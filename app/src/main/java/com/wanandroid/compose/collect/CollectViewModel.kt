package com.wanandroid.compose.collect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/28.
 */
@HiltViewModel
class CollectViewModel @Inject constructor(
    private val collectRepository: CollectRepository
) : ViewModel() {

    val collectList = collectRepository.getCollectList()
        .flow
        .cachedIn(viewModelScope)

    private val _unCollectIdState = MutableStateFlow(0)
    val unCollectIdState = _unCollectIdState.asStateFlow()

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            collectRepository.unCollectArticle(id).apply {
                onSuccess {
                    UserManager.instance.removeCollectId(id)
                    _unCollectIdState.value = id
                }
                onFailure {
                    //todo 处理失败逻辑
                }
            }
        }
    }

    init {
        Log.e("CollectViewModel", "init")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("CollectViewModel", "onCleared")
    }
}