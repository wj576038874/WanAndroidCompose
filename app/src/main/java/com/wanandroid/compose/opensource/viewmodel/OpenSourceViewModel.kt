package com.wanandroid.compose.opensource.viewmodel

import androidx.lifecycle.ViewModel
import com.wanandroid.compose.opensource.repository.OpenSourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class OpenSourceViewModel @Inject constructor(repository: OpenSourceRepository) : ViewModel() {
    private val _libraries = MutableStateFlow(repository.getLibraries())
    val libraries = _libraries.asStateFlow()
}
