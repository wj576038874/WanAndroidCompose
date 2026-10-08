package com.wanandroid.compose.bookmark

import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.repository.BookMarkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BrowserBookMarkSaverTest {
    @Test
    fun `saves current URL using matching title without any navigation`() = runTest {
        val repository = FakeRepository()
        val result = BrowserBookMarkSaver(repository).save("https://example.com/page", "Article title", "https://example.com/page")
        assertEquals(BrowserBookMarkResult.SAVED, result)
        assertEquals("Article title", repository.items.single().name)
        assertEquals("https://example.com/page", repository.items.single().link)
    }

    @Test
    fun `redirected page uses its own host rather than stale article title`() = runTest {
        val repository = FakeRepository()
        BrowserBookMarkSaver(repository).save("https://other.example/current", "Old title", "https://example.com/original")
        assertEquals("other.example", repository.items.single().name)
        assertEquals("https://other.example/current", repository.items.single().link)
    }

    @Test
    fun `rapid repeated clicks save a link only once`() = runTest {
        val repository = FakeRepository()
        repository.saveDelay = 100
        val saver = BrowserBookMarkSaver(repository)
        val results = List(3) { async { saver.save("https://example.com") } }.awaitAll()
        assertEquals(1, repository.items.size)
        assertEquals(1, results.count { it == BrowserBookMarkResult.SAVED })
        assertEquals(2, results.count { it == BrowserBookMarkResult.ALREADY_SAVED })
    }

    @Test
    fun `invalid URLs do not access repository`() = runTest {
        val repository = FakeRepository()
        val saver = BrowserBookMarkSaver(repository)
        listOf(null, "", "javascript:alert(1)", "file:///data/file", "https://bad host").forEach {
            assertEquals(BrowserBookMarkResult.INVALID_LINK, saver.save(it))
        }
        assertEquals(0, repository.loads)
        assertTrue(repository.items.isEmpty())
    }

    @Test
    fun `login network and save errors report failure`() = runTest {
        val repository = FakeRepository()
        val saver = BrowserBookMarkSaver(repository)
        repository.loadFailure = IOException()
        assertEquals(BrowserBookMarkResult.FAILED, saver.save("https://example.com"))
        repository.loadFailure = null
        repository.saveFailure = IOException()
        assertEquals(BrowserBookMarkResult.FAILED, saver.save("https://example.com"))
        assertTrue(repository.items.isEmpty())
    }

    @Test
    fun `slow request times out within broadcast deadline`() = runTest {
        val repository = FakeRepository().apply { saveDelay = 60_000 }
        assertEquals(BrowserBookMarkResult.FAILED, BrowserBookMarkSaver(repository).save("https://example.com"))
        assertTrue(testScheduler.currentTime < 10_000)
        assertTrue(repository.items.isEmpty())
    }

    @Test
    fun `caller cancellation is propagated`() = runTest {
        val repository = FakeRepository().apply { loadFailure = CancellationException() }
        try {
            BrowserBookMarkSaver(repository).save("https://example.com")
            fail("Expected cancellation")
        } catch (_: CancellationException) { }
    }

    private class FakeRepository : BookMarkRepository {
        val items = mutableListOf<BookMarkItem>()
        var loads = 0
        var loadFailure: Throwable? = null
        var saveFailure: Throwable? = null
        var saveDelay = 0L
        override suspend fun getBookMarks(): Result<List<BookMarkItem>> {
            loads++
            loadFailure?.let { throw it }
            return Result.success(items.toList())
        }
        override suspend fun addBookMark(name: String, link: String): Result<Unit> {
            delay(saveDelay)
            saveFailure?.let { return Result.failure(it) }
            items += BookMarkItem(items.size, name, link)
            return Result.success(Unit)
        }
        override suspend fun updateBookMark(id: Int, name: String, link: String): Result<Unit> = error("unused")
        override suspend fun deleteBookMark(id: Int): Result<Unit> = error("unused")
    }
}
