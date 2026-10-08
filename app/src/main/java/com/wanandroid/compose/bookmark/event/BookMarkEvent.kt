package com.wanandroid.compose.bookmark.event

import androidx.annotation.StringRes

sealed interface BookMarkEvent {
    data class Message(@param:StringRes val resourceId: Int) : BookMarkEvent
    data class OpenLink(val link: String) : BookMarkEvent
}
