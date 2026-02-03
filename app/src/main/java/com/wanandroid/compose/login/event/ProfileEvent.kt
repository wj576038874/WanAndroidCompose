package com.wanandroid.compose.login.event

import androidx.navigation3.runtime.NavKey

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class ProfileEvent {
    class Navigation(key: NavKey) : ProfileEvent()
}