package com.wanandroid.compose.bookmark.api

import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.BookMarkItem
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BookMarkApi {
    @GET("lg/collect/usertools/json")
    suspend fun getBookMarks(): BaseResponse<List<BookMarkItem>>

    @FormUrlEncoded
    @POST("lg/collect/addtool/json")
    suspend fun addBookMark(@Field("name") name: String, @Field("link") link: String): BaseResponse<Any?>

    @FormUrlEncoded
    @POST("lg/collect/updatetool/json")
    suspend fun updateBookMark(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("link") link: String,
    ): BaseResponse<Any?>

    @FormUrlEncoded
    @POST("lg/collect/deletetool/json")
    suspend fun deleteBookMark(@Field("id") id: Int): BaseResponse<Any?>
}
