package com.wanandroid.compose.route

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.wanandroid.compose.login.LoginScreen

/**
 * Created by wenjie on 2026/02/02.
 */
fun EntryProviderScope<NavKey>.forLoginScreen(navigator: Navigator) {
    entry<RouteNavKey.Login>(
        metadata = NavDisplay.transitionSpec {
            slideInVertically(
                initialOffsetY = { it }, animationSpec = tween(500)
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        } + NavDisplay.popTransitionSpec {
            // Slide old content down, revealing the new content in place underneath
            EnterTransition.None togetherWith slideOutVertically(
                targetOffsetY = { it }, animationSpec = tween(500)
            )
        } + NavDisplay.predictivePopTransitionSpec {
            // Slide old content down, revealing the new content in place underneath
            EnterTransition.None togetherWith slideOutVertically(
                targetOffsetY = { it }, animationSpec = tween(500)
            )
        }
    ) { routeNavKey ->
        LoginScreen(
            onBackClick = navigator::goBack,
            onLogin = {
                routeNavKey.redirectToKey?.let {
                    navigator.goTo(it)
                }
            }
        )
    }
}
