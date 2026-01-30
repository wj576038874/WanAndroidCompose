//package com.wanandroid.compose.collect
//
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import com.wanandroid.compose.bean.ArticleItem
//import jakarta.inject.Inject
//
///**
// * Created by wenjie on 2026/01/30.
// */
//class LocalCollectRepository @Inject constructor() : CollectRepository {
//
//    override fun getCollectList(): Pager<Int, ArticleItem> = Pager(
//        pagingSourceFactory = { CollectPagingSource(collectApi) },
//        config = PagingConfig(
//            pageSize = 20,
//            initialLoadSize = 20,
//            enablePlaceholders = false,
//            prefetchDistance = 1,
//        )
//    )
//
//    override suspend fun unCollectArticle(id: Int) = runCatching {
//
//    }
//}