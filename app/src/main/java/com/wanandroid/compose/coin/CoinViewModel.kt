package com.wanandroid.compose.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.analytics.Analytics
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/27.
 */
@HiltViewModel
class CoinViewModel @Inject constructor(
    coinRepository: CoinRepository,
    analytics: Analytics
) : ViewModel(), Analytics by analytics {

    val coinList = coinRepository.getCoinList()
        .flow
        .cachedIn(viewModelScope)

}