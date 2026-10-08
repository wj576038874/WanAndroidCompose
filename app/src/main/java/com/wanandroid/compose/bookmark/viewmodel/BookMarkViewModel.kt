package com.wanandroid.compose.bookmark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.BookMarkItem
import com.wanandroid.compose.bookmark.action.BookMarkAction
import com.wanandroid.compose.bookmark.event.BookMarkEvent
import com.wanandroid.compose.bookmark.repository.BookMarkApiException
import com.wanandroid.compose.bookmark.repository.BookMarkRepository
import com.wanandroid.compose.bookmark.state.BookMarkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@HiltViewModel
class BookMarkViewModel @Inject constructor(
    private val repository: BookMarkRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookMarkUiState())
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<BookMarkEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        refresh()
    }

    fun onAction(action: BookMarkAction) {
        when (action) {
            BookMarkAction.Refresh -> refresh()
            BookMarkAction.Add -> openEditor(null)
            is BookMarkAction.Edit -> openEditor(action.item)
            is BookMarkAction.NameChanged -> if (!_uiState.value.isSubmitting) {
                _uiState.update { it.copy(name = action.name, nameInvalid = false, mutationFailed = false) }
            }
            is BookMarkAction.LinkChanged -> if (!_uiState.value.isSubmitting) {
                _uiState.update { it.copy(link = action.link, linkInvalid = false, mutationFailed = false) }
            }
            BookMarkAction.Save -> save()
            BookMarkAction.DismissEditor -> if (!_uiState.value.isSubmitting) {
                _uiState.update { it.copy(showEditor = false) }
            }
            is BookMarkAction.RequestDelete -> if (!_uiState.value.isBusy) {
                _uiState.update {
                    it.copy(deleteTarget = action.item, mutationFailed = false, mutationError = null)
                }
            }
            BookMarkAction.ConfirmDelete -> delete()
            BookMarkAction.DismissDelete -> if (!_uiState.value.isSubmitting) {
                _uiState.update { it.copy(deleteTarget = null) }
            }
            is BookMarkAction.Open -> {
                val link = action.item.link.trim()
                _events.trySend(
                    if (isWebUrl(link)) BookMarkEvent.OpenLink(link)
                    else BookMarkEvent.Message(R.string.string_bookmark_open_failed)
                )
            }
        }
    }

    private fun refresh() {
        val state = _uiState.value
        if (state.isBusy || state.showEditor || state.deleteTarget != null) return
        _uiState.update { it.copy(isLoading = true, loadFailed = false, loadError = null) }
        viewModelScope.launch { loadBookMarks() }
    }

    private suspend fun loadBookMarks() {
        repository.getBookMarks().fold(
            onSuccess = { items ->
                _uiState.update { it.copy(items = items, isLoading = false, loadFailed = false, loadError = null) }
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(isLoading = false, loadFailed = true, loadError = error.apiMessage())
                }
            },
        )
    }

    private fun openEditor(item: BookMarkItem?) {
        if (_uiState.value.isBusy) return
        _uiState.update {
            it.copy(
                showEditor = true,
                editingId = item?.id,
                name = item?.name.orEmpty(),
                link = item?.link.orEmpty(),
                nameInvalid = false,
                linkInvalid = false,
                mutationFailed = false,
                mutationError = null,
            )
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isBusy || !state.showEditor) return
        val name = state.name.trim()
        val link = state.link.trim()
        val nameInvalid = name.isBlank()
        val linkInvalid = !isWebUrl(link)
        if (nameInvalid || linkInvalid) {
            _uiState.update { it.copy(nameInvalid = nameInvalid, linkInvalid = linkInvalid) }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, mutationFailed = false, mutationError = null) }
        viewModelScope.launch {
            val result = state.editingId?.let { repository.updateBookMark(it, name, link) }
                ?: repository.addBookMark(name, link)
            result.fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(
                            items = current.items.map {
                                if (it.id == state.editingId) it.copy(name = name, link = link) else it
                            },
                            showEditor = false,
                            name = "",
                            link = "",
                        )
                    }
                    reloadAfterMutation(R.string.string_bookmark_save_success)
                },
                onFailure = ::mutationFailed,
            )
        }
    }

    private fun delete() {
        val state = _uiState.value
        val target = state.deleteTarget ?: return
        if (state.isBusy) return
        _uiState.update { it.copy(isSubmitting = true, mutationFailed = false, mutationError = null) }
        viewModelScope.launch {
            repository.deleteBookMark(target.id).fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(items = current.items.filterNot { it.id == target.id }, deleteTarget = null)
                    }
                    reloadAfterMutation(R.string.string_bookmark_delete_success)
                },
                onFailure = ::mutationFailed,
            )
        }
    }

    private suspend fun reloadAfterMutation(messageId: Int) {
        _uiState.update { it.copy(isSubmitting = false, isLoading = true, loadFailed = false, loadError = null) }
        _events.trySend(BookMarkEvent.Message(messageId))
        loadBookMarks()
    }

    private fun mutationFailed(error: Throwable) {
        _uiState.update {
            it.copy(isSubmitting = false, mutationFailed = true, mutationError = error.apiMessage())
        }
    }

    private fun Throwable.apiMessage(): String? =
        (this as? BookMarkApiException)?.message?.takeIf { it.isNotBlank() }

    private fun isWebUrl(link: String): Boolean =
        (link.startsWith("https://", ignoreCase = true) || link.startsWith("http://", ignoreCase = true)) &&
            link.none { it.isWhitespace() } && link.toHttpUrlOrNull() != null
}
