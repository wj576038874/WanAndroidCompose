package com.wanandroid.compose.http

import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.CoinItem
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/01/27.
 */
interface CoinApi {

    /**
     * 获取积分列表
     */
    @GET("lg/coin/list/{pageNum}/json")
    suspend fun getCoinList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<CoinItem>>
}