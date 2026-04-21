package com.wanandroid.compose.search

import androidx.paging.Pager
import com.wanandroid.compose.bean.ArticleItem

/**
 * 搜索 Repository 接口
 */
interface SearchRepository {

    /**
     * 搜索文章
     * @param keyword 搜索关键词
     */
    fun searchArticles(keyword: String): Pager<Int, ArticleItem>
}
