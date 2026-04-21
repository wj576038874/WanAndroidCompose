package com.wanandroid.compose.share

import androidx.paging.Pager

/**
 * Created by wenjie on 2026/04/21.
 */
interface ShareRepository {

    fun getShareArticleList(): Pager<Int, ShareArticleItem>

    suspend fun addShareArticle(title: String, link: String): Result<Any?>

    suspend fun deleteShareArticle(id: Int): Result<Any?>
}
