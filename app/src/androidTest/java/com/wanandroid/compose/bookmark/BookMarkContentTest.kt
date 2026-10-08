package com.wanandroid.compose.bookmark

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.action.BookMarkAction
import com.wanandroid.compose.bookmark.screen.BookMarkContent
import com.wanandroid.compose.bookmark.state.BookMarkUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BookMarkContentTest {
    @get:Rule val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val item = BookMarkItem(7, "Android", "https://developer.android.com")

    @Test
    fun emptyListOffersAdd() {
        val actions = mutableListOf<BookMarkAction>()
        compose.setContent {
            MaterialTheme { BookMarkContent(BookMarkUiState(), actions::add, {}) }
        }
        compose.onNodeWithText(context.getString(R.string.string_bookmark_empty)).assertIsDisplayed()
        compose.onNodeWithContentDescription(context.getString(R.string.string_bookmark_add)).performClick()
        assertEquals(listOf(BookMarkAction.Add), actions)
    }

    @Test
    fun rowsOpenEditAndRequestDeletion() {
        val actions = mutableListOf<BookMarkAction>()
        compose.setContent {
            MaterialTheme { BookMarkContent(BookMarkUiState(items = listOf(item)), actions::add, {}) }
        }
        compose.onNodeWithText("Android").performClick()
        compose.onNodeWithContentDescription(context.getString(R.string.string_bookmark_edit)).performClick()
        compose.onNodeWithContentDescription(context.getString(R.string.string_delete)).performClick()
        assertEquals(listOf(BookMarkAction.Open(item), BookMarkAction.Edit(item), BookMarkAction.RequestDelete(item)), actions)
    }

    @Test
    fun editorShowsValidationAndDisablesSaveWhileSubmitting() {
        val actions = mutableListOf<BookMarkAction>()
        val state = mutableStateOf(BookMarkUiState(showEditor = true, nameInvalid = true, linkInvalid = true))
        compose.setContent {
            MaterialTheme { BookMarkContent(state.value, actions::add, {}) }
        }
        compose.onNodeWithText(context.getString(R.string.string_bookmark_name_invalid)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_bookmark_link_invalid)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_bookmark_name)).performTextInput("Example")
        compose.onNodeWithText(context.getString(R.string.string_bookmark_save)).performClick()
        assertEquals(listOf(BookMarkAction.NameChanged("Example"), BookMarkAction.Save), actions)
        compose.runOnIdle { state.value = state.value.copy(isSubmitting = true) }
        compose.onNodeWithText(context.getString(R.string.string_bookmark_saving)).assertIsNotEnabled()
    }

    @Test
    fun deleteDialogShowsTargetAndExplicitConfirmation() {
        val actions = mutableListOf<BookMarkAction>()
        compose.setContent {
            MaterialTheme { BookMarkContent(BookMarkUiState(deleteTarget = item), actions::add, {}) }
        }
        compose.onNodeWithText(context.getString(R.string.string_bookmark_delete_confirm, "Android")).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_confirm)).performClick()
        assertEquals(listOf(BookMarkAction.ConfirmDelete), actions)
    }

    @Test
    fun refreshErrorOffersRetryWithoutHidingExistingRows() {
        val actions = mutableListOf<BookMarkAction>()
        compose.setContent {
            MaterialTheme {
                BookMarkContent(BookMarkUiState(items = listOf(item), loadFailed = true), actions::add, {})
            }
        }
        compose.onNodeWithText("Android").assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_bookmark_load_failed)).assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_bookmark_retry)).performClick()
        assertEquals(listOf(BookMarkAction.Refresh), actions)
    }
}
