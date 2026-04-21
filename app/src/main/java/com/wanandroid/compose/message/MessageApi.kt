package com.wanandroid.compose.message

import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.MessageItem
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by wenjie on 2026/04/20.
 */
interface MessageApi {

    @GET("message/lg/count_unread/json")
    suspend fun getUnreadCount(): BaseResponse<Int>

    @GET("message/lg/unread_list/{pageNum}/json")
    suspend fun getUnreadMessageList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<MessageItem>>

    @GET("message/lg/readed_list/{pageNum}/json")
    suspend fun getReadMessageList(@Path("pageNum") pageNum: Int): BaseResponse<BasePageData<MessageItem>>
}
