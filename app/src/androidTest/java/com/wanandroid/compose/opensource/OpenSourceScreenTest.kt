package com.wanandroid.compose.opensource

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import com.wanandroid.compose.R
import com.wanandroid.compose.opensource.repository.impl.LocalOpenSourceRepository
import com.wanandroid.compose.opensource.screen.OpenSourceContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OpenSourceScreenTest {
    @get:Rule val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun descriptionsAndScrolledRowsOpenTheMatchingRepository() {
        var openedUrl: String? = null
        val libraries = LocalOpenSourceRepository().getLibraries()
        compose.setContent {
            MaterialTheme {
                OpenSourceContent(libraries, {}, { openedUrl = it.githubUrl })
            }
        }
        compose.onNodeWithText("Jetpack Compose").assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.string_oss_compose)).assertIsDisplayed()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Retrofit"))
        compose.onNodeWithText("Retrofit").performClick()
        assertEquals("https://github.com/square/retrofit", openedUrl)
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Gradle"))
        compose.onNodeWithText("Gradle").performClick()
        assertEquals("https://github.com/gradle/gradle", openedUrl)
    }

    @Test fun toolbarReturnsToPreviousPage() {
        var backCalls = 0
        compose.setContent {
            MaterialTheme {
                OpenSourceContent(LocalOpenSourceRepository().getLibraries(), { backCalls++ }, {})
            }
        }
        compose.onNodeWithContentDescription(context.getString(R.string.string_code)).performClick()
        assertEquals(1, backCalls)
    }
}
