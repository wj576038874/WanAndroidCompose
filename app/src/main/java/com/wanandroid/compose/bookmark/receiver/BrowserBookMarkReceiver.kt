package com.wanandroid.compose.bookmark.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wanandroid.compose.R
import com.wanandroid.compose.bookmark.BrowserBookMarkResult
import com.wanandroid.compose.bookmark.BrowserBookMarkSaver
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@AndroidEntryPoint
class BrowserBookMarkReceiver : BroadcastReceiver() {
    @Inject lateinit var saver: BrowserBookMarkSaver

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_ADD) return
        val pendingResult = goAsync()
        // This scope belongs to this broadcast, bounded by the saver's timeout and finish below.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        launchBroadcastTask(scope, onFinished = pendingResult::finish) {
            val result = saver.save(
                link = intent.dataString,
                title = intent.getStringExtra(EXTRA_TITLE),
                originalLink = intent.getStringExtra(EXTRA_ORIGINAL_LINK),
            )
            val message = when (result) {
                BrowserBookMarkResult.SAVED -> R.string.string_bookmark_save_success
                BrowserBookMarkResult.ALREADY_SAVED -> R.string.string_bookmark_already_saved
                BrowserBookMarkResult.INVALID_LINK -> R.string.string_bookmark_link_invalid
                BrowserBookMarkResult.FAILED -> R.string.string_bookmark_browser_failed
            }
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val ACTION_ADD = "com.wanandroid.compose.action.ADD_BROWSER_BOOKMARK"
        const val EXTRA_TITLE = "bookmark_title"
        const val EXTRA_ORIGINAL_LINK = "bookmark_original_link"
    }
}
