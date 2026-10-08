package com.wanandroid.compose.utils

import android.app.PendingIntent
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Build
import com.wanandroid.compose.bookmark.receiver.BrowserBookMarkReceiver
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test

class BrowserUtilTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun customTabReplacesBrowserBookmarkWithAppMenu() {
        var launched: Intent? = null
        val capturingContext = object : ContextWrapper(context) {
            override fun startActivity(intent: Intent, options: Bundle?) { launched = intent }
        }
        launchCustomChromeTab(capturingContext, "https://example.com/original".toUri(), toolbarColor = 0)
        val intent = requireNotNull(launched)
        assertTrue(intent.getBooleanExtra(CustomTabsIntent.EXTRA_DISABLE_BOOKMARKS_BUTTON, false))
        val menus = IntentCompat.getParcelableArrayListExtra(intent, CustomTabsIntent.EXTRA_MENU_ITEMS, Bundle::class.java)
        assertEquals(1, menus?.size)
        val menu = requireNotNull(menus).single()
        assertFalse(menu.getString(CustomTabsIntent.KEY_MENU_ITEM_TITLE).isNullOrBlank())
        @Suppress("DEPRECATION")
        val callback = menu.getParcelable<PendingIntent>(CustomTabsIntent.KEY_PENDING_INTENT)
        assertTrue(requireNotNull(callback).isBroadcast)
        assertEquals(context.packageName, callback.creatorPackage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) assertFalse(callback.isImmutable)
    }

    @Test
    fun callbackTargetsPrivateReceiverAndUsesCurrentUrl() {
        val callback = browserBookMarkIntent(context, "https://example.com/original".toUri(), "Article &amp; title")
        assertNull(callback.data)
        callback.fillIn(Intent().setData("https://example.com/redirected?q=1".toUri()), 0)
        assertEquals("https://example.com/redirected?q=1", callback.dataString)
        assertEquals("Article & title", callback.getStringExtra(BrowserBookMarkReceiver.EXTRA_TITLE))
        assertEquals(BrowserBookMarkReceiver::class.java.name, callback.component?.className)
        assertEquals(0, callback.flags and Intent.FLAG_ACTIVITY_NEW_TASK)
        @Suppress("DEPRECATION")
        val receiver = context.packageManager.getReceiverInfo(requireNotNull(callback.component), 0)
        assertFalse(receiver.exported)
        val anotherTab = browserBookMarkIntent(context, "https://example.com/another".toUri(), "Other")
        assertFalse(callback.filterEquals(anotherTab))
    }
}
