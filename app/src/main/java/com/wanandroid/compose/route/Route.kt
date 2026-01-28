package com.wanandroid.compose.route

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.bean.ArticleItem
import kotlinx.serialization.Serializable

/**
 * Created by wenjie on 2026/01/22.
 */
@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Main : Route {
        @Serializable
        data object Home : Route

        @Serializable
        data object QuestionAnswer : Route

        @Serializable
        data object Navigation : Route

        @Serializable
        object Profile : Route
    }

    @Serializable
    data class ArticleDetail(val articleItem: ArticleItem) : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Coin : Route

    @Serializable
    data object Collect : Route

    @Serializable
    data object Camera : Route

    @Serializable
    data object Search : Route

    @Serializable
    data class CameraBitmapPreview(val bitmap: ImageBitmap) : Route
}