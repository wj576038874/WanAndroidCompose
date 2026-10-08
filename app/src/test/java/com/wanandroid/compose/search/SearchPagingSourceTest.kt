package com.wanandroid.compose.search

import androidx.paging.PagingSource
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.BasePageData
import com.wanandroid.compose.bean.BaseResponse
import com.wanandroid.compose.bean.HotSearchKeyword
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class SearchPagingSourceTest {
    private val params = PagingSource.LoadParams.Refresh<Int>(null, 20, false)

    @Test
    fun `successful search forwards keyword and loads subsequent pages`() = runTest {
        val article = ArticleItem(9, "Compose", "https://example.com", "Today", "Author", "", 0, "", 0, "", "Android", "Compose", false)
        val requests = mutableListOf<Pair<Int, String>>()
        val api = object : SearchApi {
            override suspend fun getHotSearchKeywords() = BaseResponse<List<HotSearchKeyword>>(data = emptyList())
            override suspend fun searchArticles(page: Int, keyword: String): BaseResponse<BasePageData<ArticleItem>> {
                requests += page to keyword
                return BaseResponse(data = BasePageData(page + 1, listOf(article), page * 20, page == 1, 2, 20, 21))
            }
        }
        val source = SearchPagingSource(api, "Compose")
        val first = source.load(params) as PagingSource.LoadResult.Page
        assertEquals(listOf(article), first.data)
        assertNull(first.prevKey)
        assertEquals(1, first.nextKey)
        val second = source.load(PagingSource.LoadParams.Append(1, 20, false)) as PagingSource.LoadResult.Page
        assertEquals(0, second.prevKey)
        assertNull(second.nextKey)
        assertEquals(listOf(0 to "Compose", 1 to "Compose"), requests)
    }

    @Test
    fun `server error is reported even if response contains data`() = runTest {
        val source = SearchPagingSource(api(BaseResponse(data = emptyPage(), code = -1, message = "Rejected")), "compose")
        val result = source.load(params)
        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals("Rejected", (result as PagingSource.LoadResult.Error).throwable.message)
    }

    @Test
    fun `empty page terminates pagination`() = runTest {
        val result = SearchPagingSource(api(BaseResponse(data = emptyPage())), "compose").load(params)
        assertNull((result as PagingSource.LoadResult.Page).nextKey)
    }

    @Test
    fun `cancellation propagates when changing keywords`() = runTest {
        val api = object : SearchApi {
            override suspend fun getHotSearchKeywords() = BaseResponse<List<HotSearchKeyword>>(data = emptyList())
            override suspend fun searchArticles(page: Int, keyword: String): BaseResponse<BasePageData<ArticleItem>> {
                throw CancellationException("New keyword")
            }
        }
        try {
            SearchPagingSource(api, "old").load(params)
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            // The previous search must stop instead of producing an error page.
        }
    }

    private fun emptyPage() = BasePageData<ArticleItem>(1, emptyList(), 0, false, 1, 20, 0)

    private fun api(response: BaseResponse<BasePageData<ArticleItem>>) = object : SearchApi {
        override suspend fun getHotSearchKeywords() = BaseResponse<List<HotSearchKeyword>>(data = emptyList())
        override suspend fun searchArticles(page: Int, keyword: String) = response
    }
}
