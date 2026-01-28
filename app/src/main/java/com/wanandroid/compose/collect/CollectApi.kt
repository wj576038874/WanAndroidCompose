package com.wanandroid.compose.collect

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/01/28.
 */
interface CollectApi {

    @GET("lg/collect/list/{pageNum}/json")
    suspend fun getCollectList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<ArticleItem>>
}