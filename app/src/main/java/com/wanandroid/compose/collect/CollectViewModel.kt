package com.wanandroid.compose.collect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/28.
 */
class CollectViewModel(
    private val collectRepository: CollectRepository
) : ViewModel() {

    val collectList = collectRepository.getCollectList()
        .flow
        .cachedIn(viewModelScope)

    private val _unCollectIdState = MutableStateFlow(0)
    val unCollectIdState = _unCollectIdState.asStateFlow()

    fun unCollectArticle(id: Int){
        viewModelScope.launch {
            runCatching {
                collectRepository.unCollectArticle(id)
            }.onSuccess {
                if (it.code == 0) {
                    UserManager.instance.removeCollectId(id)
                    _unCollectIdState.value = id
                }
            }.onFailure {

            }
        }
    }
}