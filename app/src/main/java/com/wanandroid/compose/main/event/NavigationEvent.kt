package com.wanandroid.compose.main.event

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class NavigationEvent {
    class NavigationError(val errorMsg: String) : NavigationEvent()
}