package com.wanandroid.compose.bookmark.state

import com.wanandroid.compose.bean.BookMarkItem

data class BookMarkUiState(
    val items: List<BookMarkItem> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val loadError: String? = null,
    val isSubmitting: Boolean = false,
    val showEditor: Boolean = false,
    val editingId: Int? = null,
    val name: String = "",
    val link: String = "",
    val nameInvalid: Boolean = false,
    val linkInvalid: Boolean = false,
    val deleteTarget: BookMarkItem? = null,
    val mutationFailed: Boolean = false,
    val mutationError: String? = null,
) {
    val isBusy: Boolean get() = isLoading || isSubmitting
}
