package com.wanandroid.compose.main.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.R
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.bean.UserInfo
import com.wanandroid.compose.route.Route

/**
 * Created by wenjie on 2026/01/22.
 */

private val ITEMS = listOf(
    Triple(0, Icons.Outlined.StarOutline, R.string.string_coin),
    Triple(1, Icons.Outlined.Share, R.string.string_share),
    Triple(2, Icons.Outlined.FavoriteBorder, R.string.string_collect),
    Triple(3, Icons.Outlined.Bookmarks, R.string.string_book_mark),
    Triple(4, Icons.Outlined.History, R.string.string_history),
    Triple(5, Icons.Outlined.Grass, R.string.string_code),
    Triple(6, Icons.Outlined.Info, R.string.string_about),
    Triple(7, Icons.Outlined.Settings, R.string.string_settings),
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    val backStack = LocalBackStack.current
    val userInfo by UserManager.instance.userInfo.collectAsStateWithLifecycle()
    val isLogin by UserManager.instance.isLogin.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Header(
                innerPadding = innerPadding,
                toLogin = {
                    backStack.add(Route.Login)
                },
                userInfo = userInfo,
            )
        }
        items(ITEMS) {
            ProfileItem(
                modifier = modifier,
                item = it,
                onClick = {
                    when (it.first) {
                        0 ->{
                            if (isLogin) {
                                backStack.add(Route.Coin)
                            } else {
                                backStack.add(Route.Login)
                            }
                        }
                        7 -> {
                            if (isLogin) {
                                backStack.add(Route.Settings)
                            } else {
                                backStack.add(Route.Login)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileItemPreview(modifier: Modifier = Modifier) {
    ProfileItem(
        modifier = modifier,
        item = Triple(0, Icons.Outlined.Info, R.string.string_code),
        onClick = {})
}

@Composable
fun ProfileItem(
    modifier: Modifier = Modifier,
    item: Triple<Int, ImageVector, Int>,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = modifier.size(24.dp),
            imageVector = item.second,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = stringResource(id = item.third),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            modifier = modifier.size(18.dp),
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f),
        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    userInfo: UserInfo?,
    toLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(color = MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Spacer(modifier = modifier.weight(1f))
            IconButton(
                onClick = {

                }) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Image(
            modifier = modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.tertiary),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
        )
        Spacer(modifier = modifier.height(16.dp))

        if (userInfo != null) {
            Text(
                text = userInfo.username,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = userInfo.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                modifier = Modifier.clickable {
                    toLogin()
                },
                text = "未登录",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }

}