package com.wanandroid.compose.setting

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.LocalThemeViewModel
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.common.LoadingDialog
import com.wanandroid.compose.http.LoginApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.login.LoginRepository
import com.wanandroid.compose.login.LoginViewModel
import com.wanandroid.compose.main.state.LoginState
import kotlinx.coroutines.launch

/**
 * Created by wenjie on 2026/01/27.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val backStack = LocalBackStack.current
    val themeViewModel = LocalThemeViewModel.current
    val loginViewModel = viewModel {
        val loginApi = RetrofitHelper.create(LoginApi::class.java)
        LoginViewModel(loginRepository = LoginRepository(loginApi = loginApi))
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

    var logoutDialog by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    val isLogin by UserManager.instance.isLogin.collectAsStateWithLifecycle()
    LaunchedEffect(isLogin) {
        if (!isLogin) {
            backStack.removeLastOrNull()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(
                        text = "设置",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = {
                            backStack.removeLastOrNull()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                })
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                SettingItem(
                    item = Pair(0, "主题模式"), icon = true, onClick = {
                        openBottomSheet = !openBottomSheet
                    })
            }
            item {
                SettingItem(
                    item = Pair(1, "退出登录"), icon = false, onClick = {
                        logoutDialog = !logoutDialog
                    })
            }
        }

        if (logoutDialog) {
            AlertDialog(
                onDismissRequest = {
                    logoutDialog = false
                },
                title = {
                    Text(
                        text = "温馨提示",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = "确认退出登录吗？",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            loginViewModel.logout()
                            logoutDialog = false
                        }
                    ) {
                        Text("确认")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            logoutDialog = false
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }

        if (loginState is LoginState.Loading) {
            LoadingDialog(
                onDismissRequest = {
                    loginViewModel.cancelLogout()
                }
            )
        }

        // Sheet content
        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    openBottomSheet = false
                },
                sheetState = bottomSheetState,
            ) {
                LazyColumn {
                    items.forEach { item ->
                        item {
                            Text(
                                text = item.second,
                                textAlign = TextAlign.Center,
                                color = if (item.first == themeMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            bottomSheetState.hide()
                                        }.invokeOnCompletion {
                                            if (!bottomSheetState.isVisible) {
                                                openBottomSheet = false
                                                themeViewModel.setThemeMode(item.first)
                                            }
                                        }
                                    }
                                    .padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private val items = listOf(
    Pair(0, "跟随系统"),
    Pair(Configuration.UI_MODE_NIGHT_NO, "深色主题"),
    Pair(Configuration.UI_MODE_NIGHT_YES, "浅色主题"),
)


@Preview(
    showBackground = true,
)
@Composable
fun SettingItemPreview(modifier: Modifier = Modifier) {
    SettingItem(
        modifier = modifier,
        item = Pair(0, "设置"),
        onClick = {},
    )
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    item: Pair<Int, String>,
    icon: Boolean = true,
    onClick: () -> Unit,
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
        Spacer(modifier = modifier.width(8.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = item.second,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        AnimatedVisibility(
            visible = icon,
        ) {
            Icon(
                modifier = modifier.size(18.dp),
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            )
        }
    }
}