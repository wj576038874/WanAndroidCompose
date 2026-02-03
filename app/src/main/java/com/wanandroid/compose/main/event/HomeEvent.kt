package com.wanandroid.compose.main.event

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class HomeEvent {
    class Error(val errorMsg: String) : HomeEvent()
}