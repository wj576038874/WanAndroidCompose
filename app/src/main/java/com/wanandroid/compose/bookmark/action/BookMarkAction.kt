package com.wanandroid.compose.bookmark.action

import com.wanandroid.compose.bean.BookMarkItem

sealed interface BookMarkAction {
    data object Refresh : BookMarkAction
    data object Add : BookMarkAction
    data class Edit(val item: BookMarkItem) : BookMarkAction
    data class NameChanged(val name: String) : BookMarkAction
    data class LinkChanged(val link: String) : BookMarkAction
    data object Save : BookMarkAction
    data object DismissEditor : BookMarkAction
    data class RequestDelete(val item: BookMarkItem) : BookMarkAction
    data object ConfirmDelete : BookMarkAction
    data object DismissDelete : BookMarkAction
    data class Open(val item: BookMarkItem) : BookMarkAction
}
