package com.wanandroid.compose.share

import com.wanandroid.compose.bean.BaseResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/04/21.
 */
interface ShareApi {

    @GET("user/lg/private_articles/{pageNum}/json")
    suspend fun getShareArticleList(@Path("pageNum") pageNum: Int): BaseResponse<ShareArticleData>

    @FormUrlEncoded
    @POST("lg/user_article/add/json")
    suspend fun addShareArticle(
        @Field("title") title: String,
        @Field("link") link: String,
    ): BaseResponse<Any?>

    @POST("lg/user_article/delete/{id}/json")
    suspend fun deleteShareArticle(@Path("id") id: Int): BaseResponse<Any?>
}
