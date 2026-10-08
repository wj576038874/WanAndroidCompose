package com.wanandroid.compose.search

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.HotSearchKeyword
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 搜索 API 接口
 */
interface SearchApi {

    @GET("hotkey/json")
    suspend fun getHotSearchKeywords(): BaseResponse<List<HotSearchKeyword>>

    /**
     * 搜索文章
     * @param page 页码，从 0 开始
     * @param keyword 搜索关键词
     */
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") keyword: String
    ): BaseResponse<BasePageData<ArticleItem>>
}
