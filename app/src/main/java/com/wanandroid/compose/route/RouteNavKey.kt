package com.wanandroid.compose.route

import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.bean.ArticleItem
import kotlinx.serialization.Serializable

/**
 * Created by wenjie on 2026/01/22.
 */

/**
 * 路由
 * @param requiresLogin 是否需要登录
 */
@Serializable
sealed class RouteNavKey(val requiresLogin: Boolean = false) : NavKey {

    @Serializable
    data object Main : RouteNavKey() {
        @Serializable
        data object Home : RouteNavKey()

        @Serializable
        data object QuestionAnswer : RouteNavKey()

        @Serializable
        data object Navigation : RouteNavKey()

        @Serializable
        object Profile : RouteNavKey()
    }

    @Serializable
    data class ArticleDetail(val articleItem: ArticleItem) : RouteNavKey()

    @Serializable
    data class Login(val redirectToKey: RouteNavKey? = null) : RouteNavKey()

    @Serializable
    data object Settings : RouteNavKey()

    @Serializable
    data object Coin : RouteNavKey(requiresLogin = true)

    @Serializable
    data object Collect : RouteNavKey(requiresLogin = true)

    @Serializable
    data object Camera : RouteNavKey(requiresLogin = true)

    data object Camera2 : RouteNavKey()

    @Serializable
    data object Search : RouteNavKey(requiresLogin = true)

    @Serializable
    data object Message : RouteNavKey(requiresLogin = true)

    @Serializable
    data object Share : RouteNavKey(requiresLogin = true)

    @Serializable
    data object BookMark : RouteNavKey(requiresLogin = true)

    @Serializable
    data object History : RouteNavKey(requiresLogin = true)



    @Serializable
    data class CameraBitmapPreview(val byteArray: ByteArray) : RouteNavKey() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CameraBitmapPreview

            return byteArray.contentEquals(other.byteArray)
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }

    }
}