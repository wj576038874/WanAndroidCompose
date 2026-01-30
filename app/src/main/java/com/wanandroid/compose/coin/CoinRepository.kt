package com.wanandroid.compose.coin

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.CoinItem
import com.wanandroid.compose.coin.CoinApi
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/01/27.
 */
interface CoinRepository {
    fun getCoinList(): Pager<Int, CoinItem>
}