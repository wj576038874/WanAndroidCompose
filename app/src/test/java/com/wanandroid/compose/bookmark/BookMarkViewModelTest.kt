package com.wanandroid.compose.bookmark

import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.action.BookMarkAction
import com.wanandroid.compose.bookmark.event.BookMarkEvent
import com.wanandroid.compose.bookmark.repository.BookMarkRepository
import com.wanandroid.compose.bookmark.viewmodel.BookMarkViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class BookMarkViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeBookMarkRepository()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial load displays account bookmarks`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        assertEquals(listOf(BookMarkItem(7, "Android", "https://developer.android.com")), viewModel.uiState.value.items)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `refresh failure retains bookmarks and retry recovers`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        repository.loadFailure = IOException()
        viewModel.onAction(BookMarkAction.Refresh)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.loadFailed)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.items.size)
        repository.loadFailure = null
        viewModel.onAction(BookMarkAction.Refresh)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.loadFailed)
    }

    @Test
    fun `invalid name and non web URLs never submit`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        viewModel.onAction(BookMarkAction.Add)
        viewModel.onAction(BookMarkAction.NameChanged("  "))
        viewModel.onAction(BookMarkAction.LinkChanged("https://example.com"))
        viewModel.onAction(BookMarkAction.Save)
        assertTrue(viewModel.uiState.value.nameInvalid)
        viewModel.onAction(BookMarkAction.NameChanged("Example"))
        listOf(
            "", "example.com", "javascript:alert(1)", "file:///etc/hosts",
            "https://", "https://exa mple.com", "https:example.com", "http:/example.com",
        ).forEach { link ->
            viewModel.onAction(BookMarkAction.LinkChanged(link))
            viewModel.onAction(BookMarkAction.Save)
            assertTrue("Expected invalid URL: $link", viewModel.uiState.value.linkInvalid)
        }
        advanceUntilIdle()
        assertEquals(0, repository.saveCalls)
        assertTrue(viewModel.uiState.value.showEditor)
    }

    @Test
    fun `save trims input and blocks duplicate submit and refresh`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        viewModel.onAction(BookMarkAction.Add)
        viewModel.onAction(BookMarkAction.NameChanged(" Example "))
        viewModel.onAction(BookMarkAction.LinkChanged(" https://example.com/a?q=1 "))
        val gate = CompletableDeferred<Unit>()
        repository.saveGate = gate
        viewModel.onAction(BookMarkAction.Save)
        viewModel.onAction(BookMarkAction.Save)
        viewModel.onAction(BookMarkAction.Refresh)
        viewModel.onAction(BookMarkAction.DismissEditor)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSubmitting)
        assertTrue(viewModel.uiState.value.showEditor)
        assertEquals(1, repository.saveCalls)
        assertEquals(1, repository.loadCalls)
        gate.complete(Unit)
        advanceUntilIdle()
        assertEquals(BookMarkItem(8, "Example", "https://example.com/a?q=1"), viewModel.uiState.value.items.last())
        assertFalse(viewModel.uiState.value.showEditor)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `edit uses existing id and failed save preserves draft for retry`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        viewModel.onAction(BookMarkAction.Edit(viewModel.uiState.value.items.single()))
        assertEquals("Android", viewModel.uiState.value.name)
        viewModel.onAction(BookMarkAction.NameChanged("Updated"))
        repository.saveFailure = IOException()
        viewModel.onAction(BookMarkAction.Save)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showEditor)
        assertEquals("Updated", viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals("Android", viewModel.uiState.value.items.single().name)
        repository.saveFailure = null
        viewModel.onAction(BookMarkAction.Save)
        advanceUntilIdle()
        assertEquals(BookMarkItem(7, "Updated", "https://developer.android.com"), viewModel.uiState.value.items.single())
        assertFalse(viewModel.uiState.value.showEditor)
    }

    @Test
    fun `delete needs confirmation and failure keeps bookmark for retry`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        val item = viewModel.uiState.value.items.single()
        viewModel.onAction(BookMarkAction.RequestDelete(item))
        viewModel.onAction(BookMarkAction.DismissDelete)
        viewModel.onAction(BookMarkAction.ConfirmDelete)
        advanceUntilIdle()
        assertEquals(0, repository.deleteCalls)
        viewModel.onAction(BookMarkAction.RequestDelete(item))
        repository.deleteFailure = IOException()
        viewModel.onAction(BookMarkAction.ConfirmDelete)
        advanceUntilIdle()
        assertEquals(listOf(item), viewModel.uiState.value.items)
        assertEquals(item, viewModel.uiState.value.deleteTarget)
        assertFalse(viewModel.uiState.value.isSubmitting)
        repository.deleteFailure = null
        viewModel.onAction(BookMarkAction.ConfirmDelete)
        viewModel.onAction(BookMarkAction.ConfirmDelete)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.items.isEmpty())
        assertNull(viewModel.uiState.value.deleteTarget)
        assertEquals(2, repository.deleteCalls)
    }

    @Test
    fun `open only emits web links`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        viewModel.onAction(BookMarkAction.Open(BookMarkItem(1, "Example", " https://example.com ")))
        assertEquals(BookMarkEvent.OpenLink("https://example.com"), viewModel.events.first())
        viewModel.onAction(BookMarkAction.Open(BookMarkItem(2, "Invalid", "javascript:alert(1)")))
        assertTrue(viewModel.events.first() is BookMarkEvent.Message)
    }

    @Test
    fun `successful delete stays removed when subsequent reload fails`() = runTest(dispatcher) {
        val viewModel = BookMarkViewModel(repository)
        advanceUntilIdle()
        viewModel.onAction(BookMarkAction.RequestDelete(viewModel.uiState.value.items.single()))
        repository.loadFailure = IOException()
        viewModel.onAction(BookMarkAction.ConfirmDelete)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.items.isEmpty())
        assertTrue(viewModel.uiState.value.loadFailed)
        assertFalse(viewModel.uiState.value.isBusy)
        assertNull(viewModel.uiState.value.deleteTarget)
    }

    private class FakeBookMarkRepository : BookMarkRepository {
        private var items = listOf(BookMarkItem(7, "Android", "https://developer.android.com"))
        var loadFailure: Throwable? = null
        var saveFailure: Throwable? = null
        var deleteFailure: Throwable? = null
        var saveGate: CompletableDeferred<Unit>? = null
        var loadCalls = 0
        var saveCalls = 0
        var deleteCalls = 0

        override suspend fun getBookMarks(): Result<List<BookMarkItem>> {
            loadCalls++
            return loadFailure?.let { Result.failure(it) } ?: Result.success(items)
        }

        override suspend fun addBookMark(name: String, link: String): Result<Unit> {
            saveCalls++
            saveGate?.await()
            saveFailure?.let { return Result.failure(it) }
            items = items + BookMarkItem(8, name, link)
            return Result.success(Unit)
        }

        override suspend fun updateBookMark(id: Int, name: String, link: String): Result<Unit> {
            saveCalls++
            saveFailure?.let { return Result.failure(it) }
            items = items.map { if (it.id == id) BookMarkItem(id, name, link) else it }
            return Result.success(Unit)
        }

        override suspend fun deleteBookMark(id: Int): Result<Unit> {
            deleteCalls++
            deleteFailure?.let { return Result.failure(it) }
            items = items.filterNot { it.id == id }
            return Result.success(Unit)
        }
    }
}
