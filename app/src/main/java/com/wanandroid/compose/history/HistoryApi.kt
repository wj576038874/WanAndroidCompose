package com.wanandroid.compose.history

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 阅读历史 API 接口
 */
interface HistoryApi {

    /**
     * 获取阅读历史列表
     */
    @GET("lg/browse/history/list/{pageNum}/json")
    suspend fun getHistoryList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<ArticleItem>>

    /**
     * 删除阅读历史
     */
    @POST("lg/browse/history/delete/{id}/json")
    suspend fun deleteHistory(@Path("id") id: Int): BaseResponse<Any?>
}
