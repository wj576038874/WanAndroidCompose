package com.wanandroid.compose.main.screen

import android.text.Html
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.bean.QuestionAnswerItem
import com.wanandroid.compose.bean.Tag
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.main.api.QuestionAnswerApi
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
    val viewModel = hiltViewModel<QuestionAnswerViewModel>()
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
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                launchCustomChromeTab(
                    context = context,
                    uri = item.link?.toUri() ?: return@clickable,
                    toolbarColor = color
                )
            }
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
            )
    ) {
        val (author, niceDate, date, title, desc, chapter, collect) = createRefs()
        Text(
            text = item.author ?: "unknown",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.constrainAs(author) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        val tags = item.tags
        if (!tags.isNullOrEmpty()) {
            val tagText = tags.joinToString(separator = " & ") {
                it.name ?: ""
            }
            Text(
                text = tagText,
                modifier = Modifier
                    .constrainAs(niceDate) {
                        top.linkTo(author.top)
                        bottom.linkTo(author.bottom)
                        start.linkTo(author.end, 8.dp)
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            text = item.niceDate ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .constrainAs(date) {
                    top.linkTo(author.top)
                    bottom.linkTo(author.bottom)
                    end.linkTo(parent.end)
                }
        )
        val titleBarrier = createBottomBarrier(
            author, niceDate, date
        )
        Text(
            text = item.title ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(titleBarrier, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = Html.fromHtml(item.desc ?: "", 0).toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(desc) {
                top.linkTo(title.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = "${item.chapterName} & ${item.chapterName}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.constrainAs(chapter) {
                top.linkTo(collect.top)
                bottom.linkTo(collect.bottom)
                start.linkTo(parent.start)
            }
        )
        IconButton(
            onClick = {
                onCollectClick(!isCollected)
            },
            modifier = Modifier.constrainAs(collect) {
                top.linkTo(desc.bottom)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(
                imageVector = if (isCollected) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = Color.Red
            )
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
            title = "Kotlin 子类调用父类构造函数,Kotlin 子类调用父类构造函数,Kotlin 子类调用父类构造函数",
            time = "2023-05-31 17:12:04",
            superChapterName = "Kotlin",
            chapterName = "Kotlin 基础",
            collect = false,
            tags = listOf(
                Tag(
                    name = "Kotlin",
                    url = "https://www.wanandroid.com/tag/Kotlin"
                ),
                Tag(
                    name = "Android",
                    url = "https://www.wanandroid.com/tag/Kotlin"
                )
            ),
            niceDate = "2023-05-31",
        ),
        onCollectClick = {},
        isCollected = false,
    )
}
