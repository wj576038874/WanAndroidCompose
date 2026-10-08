package com.wanandroid.compose.search

import androidx.paging.Pager
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.HotSearchKeyword

/**
 * 搜索 Repository 接口
 */
interface SearchRepository {

    suspend fun getHotSearchKeywords(): Result<List<HotSearchKeyword>>

    /**
     * 搜索文章
     * @param keyword 搜索关键词
     */
    fun searchArticles(keyword: String): Pager<Int, ArticleItem>
}
