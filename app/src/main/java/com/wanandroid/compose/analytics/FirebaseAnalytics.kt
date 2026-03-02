package com.wanandroid.compose.analytics

import android.util.Log
import jakarta.inject.Inject

/**
 * Created by wenjie on 2026/03/02.
 */
class FirebaseAnalytics @Inject constructor() : Analytics {

    override fun logEvent(event: String) {
        Log.e("FirebaseAnalytics", event)
    }
}