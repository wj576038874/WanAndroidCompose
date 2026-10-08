package com.wanandroid.compose.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.platform.app.InstrumentationRegistry
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.bean.HotSearchKeyword
import com.wanandroid.compose.search.state.SearchUiState
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {
    @get:Rule val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val loadedStates = LoadStates(
        LoadState.NotLoading(true), LoadState.NotLoading(true), LoadState.NotLoading(true),
    )

    @Test
    fun inputDoesNotShrinkBelowMaterialMinimumAtLargeFontScale() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                MaterialTheme { SearchTextField("搜索 Compose", {}, {}, {}) }
            }
        }
        compose.onNode(hasSetTextAction()).assertHeightIsAtLeast(56.dp)
    }

    @Test
    fun searchButtonAndKeyboardSubmitAndBackReturns() {
        val queries = mutableListOf<String>()
        var backClicks = 0
        compose.setContent {
            var state by remember { mutableStateOf(SearchUiState()) }
            val results = remember { flowOf(PagingData.empty<ArticleItem>(sourceLoadStates = loadedStates)) }.collectAsLazyPagingItems()
            MaterialTheme {
                SearchContent(state, results, { state = state.copy(query = it) }, queries::add,
                    { state = SearchUiState() }, {}, { backClicks++ }, {})
            }
        }
        compose.onNode(hasSetTextAction()).performTextInput("Compose")
        compose.onNodeWithContentDescription(context.getString(R.string.string_search_submit)).performClick()
        compose.onNode(hasSetTextAction()).performImeAction()
        assertEquals(listOf("Compose", "Compose"), queries)
        compose.onNodeWithContentDescription(context.getString(R.string.string_clear)).performClick()
        assertEquals("", compose.onNode(hasSetTextAction()).fetchSemanticsNode().config[SemanticsProperties.EditableText].text)
        compose.onNodeWithContentDescription(context.getString(R.string.string_back)).performClick()
        assertEquals(1, backClicks)
    }

    @Test
    fun hotKeywordImmediatelySubmitsAndFillsInput() {
        val queries = mutableListOf<String>()
        compose.setContent {
            var state by remember { mutableStateOf(SearchUiState(hotKeywords = listOf(HotSearchKeyword(6, "面试")))) }
            val results = remember { flowOf(PagingData.empty<ArticleItem>(sourceLoadStates = loadedStates)) }.collectAsLazyPagingItems()
            MaterialTheme {
                SearchContent(state, results, {}, {
                    queries += it
                    state = state.copy(query = it, submittedQuery = it)
                }, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("面试").performClick()
        compose.onNode(hasSetTextAction()).assertTextContains("面试")
        compose.onNodeWithText(context.getString(R.string.string_search_no_result)).assertIsDisplayed()
        assertEquals(listOf("面试"), queries)
    }

    @Test
    fun resultsDisplayReadableTitlesWithoutHtmlTags() {
        val article = ArticleItem(9, "Learn <em class='highlight'>Compose</em> &amp; Kotlin", "https://example.com",
            "Today", "Author", "", 0, "", 0, "", "Android", "Compose", false)
        compose.setContent {
            val results = remember { flowOf(PagingData.from(listOf(article), sourceLoadStates = loadedStates)) }.collectAsLazyPagingItems()
            MaterialTheme {
                SearchContent(SearchUiState(query = "Compose", submittedQuery = "Compose"), results,
                    {}, {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Learn Compose & Kotlin").assertIsDisplayed()
    }
}
