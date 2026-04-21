package com.wanandroid.compose.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wanandroid.compose.share.event.ShareEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/04/21.
 */
@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareRepository: ShareRepository
) : ViewModel() {

    val shareList = shareRepository.getShareArticleList()
        .flow
        .cachedIn(viewModelScope)

    private val _shareEvent = MutableSharedFlow<ShareEvent>()
    val shareEvent = _shareEvent.asSharedFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _deletedId = MutableStateFlow(0)
    val deletedId = _deletedId.asStateFlow()

    private var addJob: Job? = null

    fun addShareArticle(title: String, link: String) {
        addJob = viewModelScope.launch {
            _isSubmitting.update { true }
            shareRepository.addShareArticle(title, link)
                .onSuccess {
                    _shareEvent.emit(ShareEvent.AddSuccess)
                }
                .onFailure {
                    _shareEvent.emit(
                        ShareEvent.Message(it.message ?: "Share article failed")
                    )
                }
            _isSubmitting.update { false }
        }
    }

    fun cancelAddShare() {
        _isSubmitting.update { false }
        addJob?.cancel()
    }

    fun deleteShareArticle(id: Int) {
        viewModelScope.launch {
            shareRepository.deleteShareArticle(id)
                .onSuccess {
                    _deletedId.value = id
                    _shareEvent.emit(ShareEvent.DeleteSuccess)
                }
                .onFailure {
                    _shareEvent.emit(
                        ShareEvent.Message(it.message ?: "Delete article failed")
                    )
                }
        }
    }
}
