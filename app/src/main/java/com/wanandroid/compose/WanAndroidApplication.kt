package com.wanandroid.compose

import android.app.Application
import android.util.Log

/**
 * Created by wenjie on 2026/01/22.
 */
class WanAndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("WanAndroidApplication", "onCreate")
    }
}