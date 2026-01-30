package com.wanandroid.compose.collect

import androidx.paging.Pager
import com.wanandroid.compose.bean.ArticleItem

/**
 * Created by wenjie on 2026/01/28.
 */
interface CollectRepository {

    fun getCollectList(): Pager<Int, ArticleItem>

    suspend fun unCollectArticle(id: Int) : Result<Any?>
}