package com.wanandroid.compose.message

import androidx.paging.Pager
import com.wanandroid.compose.bean.MessageItem

/**
 * Created by wenjie on 2026/04/20.
 */
interface MessageRepository {
    suspend fun getUnreadCount(): Result<Int>

    fun getUnreadMessageList(): Pager<Int, MessageItem>

    fun getReadMessageList(): Pager<Int, MessageItem>
}
