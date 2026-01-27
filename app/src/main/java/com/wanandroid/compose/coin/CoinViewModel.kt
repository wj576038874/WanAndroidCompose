package com.wanandroid.compose.coin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

/**
 * Created by wenjie on 2026/01/27.
 */
class CoinViewModel(
    coinRepository: CoinRepository
) : ViewModel() {

    val coinList = coinRepository.getCoinList()
        .flow
        .cachedIn(viewModelScope)

}