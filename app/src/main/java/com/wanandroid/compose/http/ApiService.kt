package com.wanandroid.compose.http

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/01/22.
 */
interface ApiService {

    @GET("banner/json")
    suspend fun getBannerList(): BaseResponse<List<BannerItem>>

    @GET("article/list/{pageNum}/json")
    suspend fun getArticleList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<ArticleItem>>
}