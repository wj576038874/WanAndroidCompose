package com.wanandroid.compose.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.HotSearchKeyword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeSearchRepository()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `submitted keyword is trimmed and fills input`() = runTest(dispatcher) {
        val viewModel = SearchViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.searchResults.collect {} }
        viewModel.search("  compose  ")
        advanceUntilIdle()
        assertEquals("compose", viewModel.uiState.value.query)
        assertEquals(listOf("compose"), repository.keywords)
    }

    @Test
    fun `same keyword can be searched again`() = runTest(dispatcher) {
        val viewModel = SearchViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.searchResults.collect {} }
        viewModel.search("compose")
        advanceUntilIdle()
        viewModel.search("compose")
        advanceUntilIdle()
        assertEquals(listOf("compose", "compose"), repository.keywords)
    }

    @Test
    fun `blank keyword does not request articles`() = runTest(dispatcher) {
        val viewModel = SearchViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.searchResults.collect {} }
        viewModel.search("   ")
        advanceUntilIdle()
        assertEquals(emptyList<String>(), repository.keywords)
    }

    @Test
    fun `hot keywords load and retry after failure`() = runTest(dispatcher) {
        repository.hotResult = Result.failure(IllegalStateException())
        val viewModel = SearchViewModel(repository)
        advanceUntilIdle()
        assertEquals(true, viewModel.uiState.value.hotKeywordsFailed)
        repository.hotResult = Result.success(listOf(HotSearchKeyword(6, "面试")))
        viewModel.loadHotKeywords()
        advanceUntilIdle()
        assertEquals(listOf(HotSearchKeyword(6, "面试")), viewModel.uiState.value.hotKeywords)
        assertEquals(false, viewModel.uiState.value.isHotKeywordsLoading)
        assertEquals(false, viewModel.uiState.value.hotKeywordsFailed)
    }

    @Test
    fun `clearing restores hot keywords and leaves no submitted query`() = runTest(dispatcher) {
        val viewModel = SearchViewModel(repository)
        viewModel.search("compose")
        viewModel.clearSearch()
        advanceUntilIdle()
        assertEquals("", viewModel.uiState.value.query)
        assertEquals("", viewModel.uiState.value.submittedQuery)
        assertEquals(listOf(HotSearchKeyword(6, "面试")), viewModel.uiState.value.hotKeywords)
    }

    private class FakeSearchRepository : SearchRepository {
        val keywords = mutableListOf<String>()
        var hotResult = Result.success(listOf(HotSearchKeyword(6, "面试")))
        override suspend fun getHotSearchKeywords() = hotResult
        override fun searchArticles(keyword: String): Pager<Int, ArticleItem> {
            keywords += keyword
            return Pager(PagingConfig(pageSize = 20)) {
                object : PagingSource<Int, ArticleItem>() {
                    override fun getRefreshKey(state: PagingState<Int, ArticleItem>): Int? = null
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> =
                        LoadResult.Page(emptyList(), null, null)
                }
            }
        }
    }
}
