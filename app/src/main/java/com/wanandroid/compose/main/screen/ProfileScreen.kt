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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wanandroid.compose.R

/**
 * Created by wenjie on 2026/01/22.
 */

private val ITEMS = listOf(
    Icons.Outlined.StarOutline to "我的积分",
    Icons.Outlined.Share to "我的分享",
    Icons.Outlined.FavoriteBorder to "我的收藏",
    Icons.Outlined.Bookmarks to "我的书签",
    Icons.Outlined.History to "阅读历史",
    Icons.Outlined.Grass to "开源项目",
    Icons.Outlined.Info to "关于作者",
    Icons.Outlined.Settings to "系统设置",
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Header(
                innerPadding = innerPadding
            )
        }
        items(ITEMS) {
            ProfileItem(
                modifier = modifier,
                item = it
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileItemPreview(modifier: Modifier = Modifier) {
    ProfileItem(
        modifier = modifier,
        item = Icons.Outlined.Info to "我的积分"
    )
}

@Composable
fun ProfileItem(
    modifier: Modifier = Modifier,
    item: Pair<ImageVector, String>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable {

            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = modifier.size(24.dp),
            imageVector = item.first,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = item.second,
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

@Preview(showBackground = true)
@Composable
fun Header(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp)
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

                }
            ) {
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
        val isLogin = false
        if (isLogin){
            Text(
                text = "wenjie",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = "1234567890",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }else{
            Text(
                modifier = Modifier.clickable{

                },
                text = "未登录",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }

}