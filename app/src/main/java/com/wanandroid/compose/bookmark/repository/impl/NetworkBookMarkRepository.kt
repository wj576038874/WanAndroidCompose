package com.wanandroid.compose.bookmark.repository.impl

import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bookmark.api.BookMarkApi
import com.wanandroid.compose.bookmark.repository.BookMarkApiException
import com.wanandroid.compose.bookmark.repository.BookMarkRepository
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException

class NetworkBookMarkRepository @Inject constructor(
    private val api: BookMarkApi,
) : BookMarkRepository {
    override suspend fun getBookMarks(): Result<List<BookMarkItem>> = request {
        requireNotNull(api.getBookMarks().checkedData())
    }

    override suspend fun addBookMark(name: String, link: String): Result<Unit> = request {
        api.addBookMark(name, link).ensureSuccess()
    }

    override suspend fun updateBookMark(id: Int, name: String, link: String): Result<Unit> = request {
        api.updateBookMark(id, name, link).ensureSuccess()
    }

    override suspend fun deleteBookMark(id: Int): Result<Unit> = request {
        api.deleteBookMark(id).ensureSuccess()
    }

    private fun <T> BaseResponse<T>.checkedData(): T? {
        ensureSuccess()
        return data
    }

    private fun BaseResponse<*>.ensureSuccess() {
        if (!isSuccess) throw BookMarkApiException(message)
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}
