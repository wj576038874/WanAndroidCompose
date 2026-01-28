package com.wanandroid.compose.coin

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.coin.CoinApi

/**
 * Created by wenjie on 2026/01/27.
 */
class CoinRepository(
    private val coinApi: CoinApi
) {
    fun getCoinList() = Pager(
        pagingSourceFactory = { CoinPagingSource(coinApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )
}