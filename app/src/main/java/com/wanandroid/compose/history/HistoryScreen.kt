package com.wanandroid.compose.history

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wanandroid.compose.R
import com.wanandroid.compose.common.CommonToolbar

/**
 * Created by wenjie on 2026/01/30.
 */
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier, onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CommonToolbar(
                title = stringResource(R.string.string_history), onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.string_history),
            )
        }
    }
}