package com.wanandroid.compose.http

import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.UserInfo
import com.wanandroid.compose.bean.UserInfoData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by wenjie on 2026/01/26.
 */
interface LoginApi {

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") password: String
    ): BaseResponse<UserInfo>


    @GET("user/lg/userinfo/json")
    suspend fun getUserCoinInfo(): BaseResponse<UserInfoData>
}