package com.wanandroid.compose.share.event

/**
 * Created by wenjie on 2026/04/21.
 */
sealed class ShareEvent {
    data class Message(val message: String) : ShareEvent()
    object AddSuccess : ShareEvent()
    object DeleteSuccess : ShareEvent()
}
