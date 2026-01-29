package com.wanandroid.compose.main

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.wanandroid.compose.bean.ArticleItem
import com.wanandroid.compose.main.bottombar.BottomNavBar
import com.wanandroid.compose.main.screen.HomeScreen
import com.wanandroid.compose.main.screen.NavigationScreen
import com.wanandroid.compose.main.screen.ProfileScreen
import com.wanandroid.compose.main.screen.QuestionAnswerScreen
import com.wanandroid.compose.route.RouteNavKey

/**
 * Created by wenjie on 2026/01/22.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onArticleItemClick: (ArticleItem) -> Unit
) {
    val backStack = rememberNavBackStack(RouteNavKey.Main.Home)
    val selected = backStack.last()
    val context = LocalContext.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                modifier = modifier,
                selected = selected,
                onClick = {
                    if (!backStack.contains(it)) {
                        backStack.add(it)
                    } else {
                        backStack.remove(it)
                        backStack.add(it)
                    }
                })
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            backStack = backStack,
            onBack = {
                if (selected == RouteNavKey.Main.Home) {
                    (context as Activity).finish()
                } else {
                    backStack.remove(selected)
                    backStack.add(RouteNavKey.Main.Home)
                }
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<RouteNavKey.Main.Home> {
                    HomeScreen(
                        innerPadding = innerPadding,
                        onArticleItemClick = onArticleItemClick
                    )
                }
                entry<RouteNavKey.Main.QuestionAnswer> {
                    QuestionAnswerScreen(
                        innerPadding = innerPadding,
                    )
                }
                entry<RouteNavKey.Main.Navigation> {
                    NavigationScreen(
                        innerPadding = innerPadding,
                    )
                }
                entry<RouteNavKey.Main.Profile> {
                    ProfileScreen(
                        innerPadding = innerPadding,
                    )
                }
            }
        )
    }
}