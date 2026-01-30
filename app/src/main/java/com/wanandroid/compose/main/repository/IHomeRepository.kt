package com.wanandroid.compose.main.repository

import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.bean.BasePageData

/**
 * Created by wenjie on 2026/01/30.
 */
interface IHomeRepository {
    suspend fun getBannerList(): Result<List<BannerItem>>

    suspend fun getArticleList(pageNum: Int): Result<BasePageData<ArticleItem>>

    suspend fun collectArticle(id: Int): Result<Any?>

    suspend fun unCollectArticle(id: Int): Result<Any?>

}