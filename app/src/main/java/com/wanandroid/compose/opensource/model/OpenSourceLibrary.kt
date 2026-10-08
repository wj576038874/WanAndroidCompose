package com.wanandroid.compose.opensource.model

import androidx.annotation.StringRes

data class OpenSourceLibrary(
    val id: String,
    val name: String,
    @param:StringRes val descriptionRes: Int,
    val githubUrl: String,
)
