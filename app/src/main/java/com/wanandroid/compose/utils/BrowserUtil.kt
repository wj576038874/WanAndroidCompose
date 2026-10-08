package com.wanandroid.compose.utils

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.text.HtmlCompat
import com.wanandroid.compose.bookmark.receiver.BrowserBookMarkReceiver
import java.util.UUID
import com.wanandroid.compose.R
import com.wanandroid.compose.MainActivity

/**
 * Created by wenjie on 2026/01/27.
 */
fun launchCustomChromeTab(
    context: Context, uri: Uri, title: String? = null, @ColorInt toolbarColor: Int
) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor)
//        .setSecondaryToolbarColor(Color.Green.toArgb())
//        .setNavigationBarColor(Color.Blue.toArgb())
//        .setNavigationBarDividerColor(Color.Yellow.toArgb())
        .build()
    val customTabsIntent = CustomTabsIntent.Builder().setSendToExternalDefaultHandlerEnabled(false)
//        .setShareState(SHARE_STATE_OFF)
        .setDefaultColorSchemeParams(customTabBarColor).setBookmarksButtonEnabled(false)
        .addMenuItem(
            context.getString(R.string.string_bookmark_add_from_browser),
            browserBookMarkPendingIntent(context, uri, title)
        ).build()

    customTabsIntent.launchUrl(context, uri)
}

internal fun browserBookMarkIntent(context: Context, uri: Uri, title: String?): Intent =
    Intent(context, BrowserBookMarkReceiver::class.java).apply {
        action = BrowserBookMarkReceiver.ACTION_ADD
        // Separate callbacks per tab so opening another tab cannot overwrite its title.
        addCategory(UUID.randomUUID().toString())
        putExtra(BrowserBookMarkReceiver.EXTRA_ORIGINAL_LINK, uri.toString())
        putExtra(BrowserBookMarkReceiver.EXTRA_TITLE, title?.let {
            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        })
        // Leave data unset: Custom Tabs fills in the current URL, including navigation/redirects.
    }

private fun browserBookMarkPendingIntent(
    context: Context, uri: Uri, title: String?
): PendingIntent {
    // Mutable is required for the current URL; the receiver is explicit and non-exported.
    val flags =
        PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
    return PendingIntent.getBroadcast(context, 0, browserBookMarkIntent(context, uri, title), flags)
}
