package com.wanandroid.compose.http

import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.NavigationItem
import retrofit2.http.GET

/**
 * Created by wenjie on 2026/01/23.
 */
interface NavigationApi {
    @GET("navi/json")
    suspend fun getNavigationList(): BaseResponse<List<NavigationItem>>
}