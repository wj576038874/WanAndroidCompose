package com.wanandroid.compose.bean

/**
 * Created by wenjie on 2026/01/23.
 */
class BasePageData<T>(
    val curPage: Int,
    val datas: List<T>?,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)