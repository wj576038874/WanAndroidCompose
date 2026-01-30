package com.wanandroid.compose.coin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.compose.R
import com.wanandroid.compose.bean.CoinItem
import com.wanandroid.compose.common.CommonToolbar
import com.wanandroid.compose.common.LazyColumnPaging
import com.wanandroid.compose.route.RouteNavKey
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by wenjie on 2026/01/27.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<CoinViewModel>()

    val lazyPagingItems = viewModel.coinList.collectAsLazyPagingItems()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CommonToolbar(
                routeNavKey = RouteNavKey.Coin,
                title = stringResource(id = R.string.string_coin),
            )
        }
    ) { innerPadding ->
        LazyColumnPaging(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            lazyPagingItems = lazyPagingItems,
            content = {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { item ->
                        item.id
                    }
                ) { index ->
                    lazyPagingItems[index]?.let { item ->
                        CoinItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            coinItem = item
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CoinItemPreview() {
    CoinItem(
        coinItem = CoinItem(
            coinCount = 10,
            date = 1674841600000,
            desc = "2026-01-26 11:55:23 签到 , 积分：11 + 22026-01-26 11:123",
            id = 100,
            reason = "测试",
            type = 1,
            userId = 100,
            userName = "测试"
        )
    )
}

@Composable
fun CoinItem(
    modifier: Modifier = Modifier,
    coinItem: CoinItem
) {
    val date  = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    ).format(coinItem.date)
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable{}
            .padding(16.dp)
    ) {
        val (tvCoinCount, tvDate, tvDesc) = createRefs()
        Text(
            text = coinItem.desc,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.constrainAs(tvDesc) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(tvDate.top, 8.dp)
                end.linkTo(tvCoinCount.start, 16.dp)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.constrainAs(tvDate) {
                top.linkTo(tvDesc.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            }
        )
        Text(
            text = "${coinItem.coinCount}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.constrainAs(tvCoinCount) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        )
    }
}


