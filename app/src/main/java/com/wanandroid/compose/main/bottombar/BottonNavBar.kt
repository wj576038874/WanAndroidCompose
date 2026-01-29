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
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.wanandroid.compose.R
import com.wanandroid.compose.route.RouteNavKey

/**
 * Created by wenjie on 2026/01/14.
 */

val BOTTOM_ITEM = mapOf(
    RouteNavKey.Main.Home to BottomBarItem(Icons.Outlined.Home, R.string.string_home),
    RouteNavKey.Main.QuestionAnswer to BottomBarItem(Icons.Outlined.QuestionAnswer, R.string.string_qa),
    RouteNavKey.Main.Navigation to BottomBarItem(Icons.Outlined.Navigation, R.string.string_navigation),
    RouteNavKey.Main.Profile to BottomBarItem(Icons.Outlined.Person, R.string.string_profile),
)

@Composable
fun BottomNavBar(
    selected: NavKey, onClick: (NavKey) -> Unit, modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier) {
        BOTTOM_ITEM.forEach { (route, item) ->
            val selected = route == selected
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = stringResource(item.label)
                    )
                },
                label = { Text(stringResource(item.label)) },
                selected = selected,
                onClick = {
                    onClick(route)
                })
        }
    }
}