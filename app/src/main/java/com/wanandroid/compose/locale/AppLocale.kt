package com.wanandroid.compose.locale

import android.content.Context
import android.content.res.Configuration
import com.wanandroid.compose.WanAndroidApplication
import java.util.Locale

/**
 * Created by wenjie on 2026/02/02.
 */
class AppLocale(val languageTag: String) {
    private val localizedContext: Context

    init {
        val locale = Locale.forLanguageTag(languageTag)
        val baseConfig = WanAndroidApplication.context.resources.configuration  // 先复制当前配置
        val newConfig = Configuration(baseConfig).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        localizedContext = WanAndroidApplication.context.createConfigurationContext(newConfig)
    }

    fun getString(id: Int): String = localizedContext.getString(id)

    fun getString(id: Int, vararg formatArgs: Any): String =
        localizedContext.getString(id, *formatArgs)

}