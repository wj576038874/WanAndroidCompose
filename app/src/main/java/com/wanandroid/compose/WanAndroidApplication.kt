package com.wanandroid.compose

import android.app.Application
import android.content.Context
import android.util.Log

/**
 * Created by wenjie on 2026/01/22.
 */
class WanAndroidApplication : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("WanAndroidApplication", "onCreate")
        context = applicationContext
    }
}