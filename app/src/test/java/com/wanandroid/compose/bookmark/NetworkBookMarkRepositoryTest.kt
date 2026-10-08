package com.wanandroid.compose.bookmark

import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.api.BookMarkApi
import com.wanandroid.compose.bookmark.repository.impl.NetworkBookMarkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class NetworkBookMarkRepositoryTest {
    private val api = FakeBookMarkApi()
    private val repository = NetworkBookMarkRepository(api)

    @Test
    fun `list exposes data and rejects server errors or missing payload`() = runBlocking {
        assertEquals(listOf(BookMarkItem(3, "Example", "https://example.com")), repository.getBookMarks().getOrThrow())
        api.listResponse = BaseResponse(code = -1001, message = "Please sign in")
        assertEquals("Please sign in", repository.getBookMarks().exceptionOrNull()?.message)
        api.listResponse = BaseResponse()
        assertTrue(repository.getBookMarks().isFailure)
    }

    @Test
    fun `mutations handle successful null payload and reject API errors`() = runBlocking {
        assertTrue(repository.addBookMark("Name", "https://example.com").isSuccess)
        assertEquals(listOf("Name", "https://example.com"), api.arguments)
        assertTrue(repository.updateBookMark(3, "Updated", "https://example.com/new").isSuccess)
        assertEquals(listOf(3, "Updated", "https://example.com/new"), api.arguments)
        assertTrue(repository.deleteBookMark(3).isSuccess)
        assertEquals(listOf(3), api.arguments)
        api.mutationResponse = BaseResponse(code = -1, message = "Rejected")
        assertTrue(repository.addBookMark("Name", "https://example.com").isFailure)
        assertTrue(repository.updateBookMark(3, "Name", "https://example.com").isFailure)
        assertTrue(repository.deleteBookMark(3).isFailure)
    }

    @Test
    fun `network errors become failures without swallowing cancellation`() = runBlocking {
        api.failure = IOException("offline")
        assertTrue(repository.getBookMarks().exceptionOrNull() is IOException)
        api.failure = CancellationException("cancelled")
        try {
            repository.getBookMarks()
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            // Expected: leaving the screen must cancel the request.
        }
    }

    private class FakeBookMarkApi : BookMarkApi {
        var listResponse = BaseResponse(data = listOf(BookMarkItem(3, "Example", "https://example.com")))
        var mutationResponse = BaseResponse<Any?>()
        var failure: Exception? = null
        var arguments: List<Any> = emptyList()

        override suspend fun getBookMarks(): BaseResponse<List<BookMarkItem>> {
            failure?.let { throw it }
            return listResponse
        }

        override suspend fun addBookMark(name: String, link: String): BaseResponse<Any?> {
            arguments = listOf(name, link)
            return mutationResponse
        }

        override suspend fun updateBookMark(id: Int, name: String, link: String): BaseResponse<Any?> {
            arguments = listOf(id, name, link)
            return mutationResponse
        }

        override suspend fun deleteBookMark(id: Int): BaseResponse<Any?> {
            arguments = listOf(id)
            return mutationResponse
        }
    }
}
