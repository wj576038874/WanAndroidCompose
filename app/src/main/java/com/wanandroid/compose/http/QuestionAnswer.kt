package com.wanandroid.compose.http

import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.QuestionAnswerItem
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/01/23.
 */
interface QuestionAnswerApi {

    @GET("wenda/list/{pageNum}/json")
    suspend fun getQuestionAnswerList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<QuestionAnswerItem>>
}