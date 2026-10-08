package com.wanandroid.compose.bookmark.repository

import com.wanandroid.compose.bean.BookMarkItem

interface BookMarkRepository {
    suspend fun getBookMarks(): Result<List<BookMarkItem>>
    suspend fun addBookMark(name: String, link: String): Result<Unit>
    suspend fun updateBookMark(id: Int, name: String, link: String): Result<Unit>
    suspend fun deleteBookMark(id: Int): Result<Unit>
}

class BookMarkApiException(message: String?) : Exception(message)
