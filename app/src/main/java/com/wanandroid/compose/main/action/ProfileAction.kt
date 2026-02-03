package com.wanandroid.compose.main.action

/**
 * Created by wenjie on 2026/02/02.
 */
sealed class ProfileAction {
    object Message : ProfileAction()
    object Coin : ProfileAction()
    object Share : ProfileAction()
    object Collect : ProfileAction()
    object Bookmark : ProfileAction()
    object History : ProfileAction()
    object Code : ProfileAction()
    object About : ProfileAction()
    object Setting : ProfileAction()
    object Login : ProfileAction()
}