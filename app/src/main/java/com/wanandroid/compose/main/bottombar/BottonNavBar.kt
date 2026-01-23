package com.wanandroid.compose.main.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.route.Route

/**
 * Created by wenjie on 2026/01/14.
 */

val BOTTOM_ITEM = mapOf(
    Route.Main.Home to BottomBarItem(Icons.Outlined.Home, "Home"),
    Route.Main.QuestionAnswer to BottomBarItem(Icons.Outlined.QuestionAnswer, "Q&A"),
    Route.Main.Navigation to BottomBarItem(Icons.Outlined.Navigation, "Navigation"),
    Route.Main.Profile to BottomBarItem(Icons.Outlined.Person, "Profile"),
)

@Composable
fun BottomNavBar(
    selected: NavKey,
    onClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier) {
        BOTTOM_ITEM.forEach { (route, item) ->
            val selected = route == selected
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    onClick(route)
                })
        }
    }
}