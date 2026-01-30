package com.wanandroid.compose.coin

import androidx.paging.Pager
import androidx.paging.PagingConfig
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/30.
 */
class NetworkCoinRepository @Inject constructor(private val coinApi: CoinApi) : CoinRepository {

    override fun getCoinList() = Pager(
        pagingSourceFactory = { CoinPagingSource(coinApi) },
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 1,
        )
    )
}