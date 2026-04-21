package com.wanandroid.compose.message

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wanandroid.compose.bean.MessageItem
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/04/20.
 */
class NetworkMessageRepository @Inject constructor(
    private val messageApi: MessageApi
) : MessageRepository {

    override suspend fun getUnreadCount() = runCatching {
        val response = messageApi.getUnreadCount()
        if (response.isSuccess) {
            response.data ?: 0
        } else {
            throw Exception(response.message)
        }
    }

    override fun getUnreadMessageList(): Pager<Int, MessageItem> {
        return createPager { page ->
            val response = messageApi.getUnreadMessageList(page)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            val data = response.data ?: throw Exception("data is null")
            MessagePage(
                items = data.datas ?: emptyList(),
                over = data.over,
            )
        }
    }

    override fun getReadMessageList(): Pager<Int, MessageItem> {
        return createPager { page ->
            val response = messageApi.getReadMessageList(page)
            if (!response.isSuccess) {
                throw Exception(response.message)
            }
            val data = response.data ?: throw Exception("data is null")
            MessagePage(
                items = data.datas ?: emptyList(),
                over = data.over,
            )
        }
    }

    private fun createPager(
        loadPage: suspend (Int) -> MessagePage
    ): Pager<Int, MessageItem> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
                prefetchDistance = 1,
            ),
            pagingSourceFactory = {
                MessagePagingSource(loadPage)
            }
        )
    }
}
