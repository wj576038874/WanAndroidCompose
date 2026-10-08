package com.wanandroid.compose.opensource.repository

import com.wanandroid.compose.opensource.model.OpenSourceLibrary

interface OpenSourceRepository {
    fun getLibraries(): List<OpenSourceLibrary>
}
