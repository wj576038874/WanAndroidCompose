package com.wanandroid.compose.route

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.R
import com.wanandroid.compose.opensource.screen.OpenSourceScreen
import com.wanandroid.compose.utils.launchCustomChromeTab

fun EntryProviderScope<NavKey>.forOpenSourceScreen(navigator: Navigator) {
    entry<RouteNavKey.OpenSource> {
        val context = LocalContext.current
        val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
        OpenSourceScreen(
            onBackClick = navigator::goBack,
            onOpenLibrary = { library ->
                try {
                    launchCustomChromeTab(context, library.githubUrl.toUri(), library.name, toolbarColor)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(context, R.string.string_oss_open_failed, Toast.LENGTH_LONG).show()
                }
            },
        )
    }
}
