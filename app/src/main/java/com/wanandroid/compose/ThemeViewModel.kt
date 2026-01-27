package com.wanandroid.compose

import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by wenjie on 2026/01/26.
 */
class ThemeViewModel : ViewModel() {

    private val sp by lazy {
        WanAndroidApplication.context.getSharedPreferences("MY_THEME", MODE_PRIVATE)
    }

    /**
     * 主题模式
     */
    private val _themeMode = MutableStateFlow(
        sp.getInt("THEME_MODE", Configuration.UI_MODE_NIGHT_YES)
    )
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    /**
     * 设置主题模式
     */
    fun setThemeMode(themeMode: Int) {
        _themeMode.value = themeMode
        sp.edit {
            putInt("THEME_MODE", themeMode)
        }
    }
}