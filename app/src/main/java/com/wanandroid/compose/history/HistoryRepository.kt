package com.wanandroid.compose.history

import androidx.paging.Pager
import com.wanandroid.compose.bean.ArticleItem

/**
 * 阅读历史 Repository 接口
 */
interface HistoryRepository {

    /**
     * 获取阅读历史列表
     */
    fun getHistoryList(): Pager<Int, ArticleItem>

    /**
     * 删除阅读历史
     */
    suspend fun deleteHistory(id: Int): Result<Any?>
}
