package com.wanandroid.compose.main.api

import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.QuestionAnswerItem
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/01/23.
 */
interface QuestionAnswerApi {

    @GET("wenda/list/{pageNum}/json")
    suspend fun getQuestionAnswerList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<QuestionAnswerItem>>

    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): BaseResponse<Any>

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollectArticle(@Path("id") id: Int): BaseResponse<Any>
}