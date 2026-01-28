package com.wanandroid.compose.collect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

/**
 * Created by wenjie on 2026/01/28.
 */
class CollectViewModel(
    collectRepository: CollectRepository
) : ViewModel() {

    val collectList = collectRepository.getCollectList()
        .flow
        .cachedIn(viewModelScope)

}