package com.wanandroid.compose.main.screen

import android.text.Html
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.bean.QuestionAnswerItem
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.main.api.QuestionAnswerApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.main.repository.QuestionAnswerRepository
import com.wanandroid.compose.main.viemodel.QuestionAnswerViewModel
import com.wanandroid.compose.utils.launchCustomChromeTab

/**
 * Created by wenjie on 2026/01/22.
 */
@Composable
fun QuestionAnswerScreen(
    modifier: Modifier = Modifier, innerPadding: PaddingValues
) {
    val viewModel = viewModel {
        val questionAnswerApi = RetrofitHelper.create(QuestionAnswerApi::class.java)
        val questionAnswerRepository = QuestionAnswerRepository(questionAnswerApi)
        QuestionAnswerViewModel(questionAnswerRepository)
    }
    val lazyPagingItems = viewModel.questionAnswerList.collectAsLazyPagingItems()
    val collectedIds by viewModel.collectedIds.collectAsStateWithLifecycle()
//    LaunchedEffect(collectIds) {
//        itemList.refresh()
//    }
    LazyColumnPaging(
        modifier = modifier.fillMaxSize(),
        lazyPagingItems = lazyPagingItems,
    ) {
        item {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey {
                it.id
            }) { index ->
            lazyPagingItems[index]?.let { item ->
                // 实时计算当前收藏状态（合并服务器原始 + 本地操作）
                val isCollected = collectedIds.contains(item.id)
                QuestionAnswerItem(
                    item = item, isCollected = isCollected, onCollectClick = {
                        if (it) {
                            viewModel.collectArticle(item.id)
                        } else {
                            viewModel.unCollectArticle(item.id)
                        }
                    })
            }
        }
    }
}

@Composable
fun QuestionAnswerItem(
    item: QuestionAnswerItem,
    isCollected: Boolean,
    onCollectClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary.toArgb()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                launchCustomChromeTab(
                    context = context,
                    uri = item.link?.toUri() ?: return@clickable,
                    toolbarColor = color
                )
            }
            .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.author ?: "unknown",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.niceDate ?: "unknown",
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Text(
                text = item.time ?: "unknown",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title ?: "unknown",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = Html.fromHtml(item.desc ?: "unknown").toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.chapterName} & ${item.chapterName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
            IconButton(
                onClick = {
                    onCollectClick(!isCollected)
                }) {
                Icon(
                    imageVector = if (isCollected) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun QuestionAnswerItemPreview() {
    QuestionAnswerItem(
        QuestionAnswerItem(
            id = 1,
            desc = "\\u003Cp\\u003E在framework的代码中，经常看到如下的权限检测的代码：\\u003C/p\\u003E\\r\\n\\u003Cp\\u003E\\u003Cimg src=\\\"https://wanandroid.com/blogimgs/af042353-c7c6-4f29-a988-3ad9b369964d.png\\\" alt=\\\"q1.png\\\" /\\u003E\\u003C/p\\u003E\\r\\n\\u003Cp\\u003E\\u003Cimg src=\\\"https://wanandroid.com/blogimgs/01fdb9cf-6f44-48bf-aa48-0cd527bfebd0.png\\\" alt=\\\"q2.png\\\" /\\u003E\\u003C/p\\u003E\\r\\n\\u003Cp\\u003EBinder.getCallingUid()字面理解是获取调用方的uid，但是这个代码是目标进程调用的，如何通过一个静态方法调用，就拿到调用方的uid呢？\\u003C/p\\u003E",
            author = "xiaoyang",
            link = "https://www.wanandroid.com/wenda/show/8857?fid=225&date=2023_05_31_17_12_04&message=package%20or#msg_id2773",
            title = "Kotlin 子类调用父类构造函数",
            time = "2023-05-31 17:12:04",
            superChapterName = "Kotlin",
            chapterName = "Kotlin 基础",
            collect = false,
            niceDate = "2023-05-31",
        ),
        onCollectClick = {},
        isCollected = false,
    )
}
