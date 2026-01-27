package com.wanandroid.compose.setting

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.LocalAppViewModel
import com.wanandroid.compose.R
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.WanAndroidApplication
import com.wanandroid.compose.common.LoadingDialog
import com.wanandroid.compose.http.LoginApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.login.LoginRepository
import com.wanandroid.compose.login.LoginViewModel
import com.wanandroid.compose.main.state.LoginState
import com.wanandroid.compose.main.state.LogoutState
import com.wanandroid.compose.route.Route
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Created by wenjie on 2026/01/27.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val backStack = LocalBackStack.current
    val themeViewModel = LocalAppViewModel.current
    val loginViewModel = viewModel {
        val loginApi = RetrofitHelper.create(LoginApi::class.java)
        LoginViewModel(loginRepository = LoginRepository(loginApi = loginApi))
    }
    val scope = rememberCoroutineScope()
    var openBottomSheetTheme by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpandedTheme by rememberSaveable { mutableStateOf(false) }
    val bottomSheetStateTheme =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpandedTheme)
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

    var openBottomSheetLanguage by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpandedLanguage by rememberSaveable { mutableStateOf(false) }
    val bottomSheetStateLanguage =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpandedLanguage)
    val language by themeViewModel.language.collectAsStateWithLifecycle()

    var logoutDialog by remember { mutableStateOf(false) }
    val logoutState by loginViewModel.logoutState.collectAsStateWithLifecycle()
//    LaunchedEffect(logoutState) {
//        if (logoutState is LogoutState.Success) {
//            backStack.removeLastOrNull()
//        }
//    }
    Scaffold(
        modifier = modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(
                        text = stringResource(id = R.string.string_settings),
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
                    item = Pair(0, R.string.string_change_theme),
                    icon = true,
                    onClick = {
                        openBottomSheetTheme = !openBottomSheetTheme
                    }
                )
            }
            item {
                SettingItem(
                    item = Pair(2, R.string.string_change_language),
                    icon = true,
                    onClick = {
                        openBottomSheetLanguage = !openBottomSheetLanguage
                    }
                )
            }
            item {
                SettingItem(
                    item = Pair(1, R.string.string_logout),
                    icon = false,
                    onClick = {
                        logoutDialog = !logoutDialog
                    }
                )
            }
        }

        if (logoutDialog) {
            LogoutDialog(
                onDismissRequest = {
                    logoutDialog = false
                },
                confirmClick = {
                    logoutDialog = false
                    loginViewModel.logout()
                },
                cancelClick = {
                    logoutDialog = false
                }
            )
        }

        when (logoutState) {
            is LogoutState.Failure -> {
                Toast.makeText(
                    WanAndroidApplication.context,
                    (logoutState as LogoutState.Failure).errorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is LogoutState.Success -> {
                backStack.remove(Route.Settings)
            }

            is LogoutState.Loading -> {
                LoadingDialog(
                    onDismissRequest = {
                        loginViewModel.cancelLogout()
                    }
                )
            }

            is LogoutState.Nothing -> {}
        }

        // Sheet content
        if (openBottomSheetTheme) {
            ModalBottomSheet(
                onDismissRequest = {
                    openBottomSheetTheme = false
                },
                sheetState = bottomSheetStateTheme,
            ) {
                LazyColumn {
                    items(themeItems) { item ->
                        BottomSheetItem(
                            text = stringResource(id = item.second),
                            isSelected = item.first == themeMode,
                            onClick = {
                                scope.launch {
                                    bottomSheetStateTheme.hide()
                                }.invokeOnCompletion {
                                    if (!bottomSheetStateTheme.isVisible) {
                                        openBottomSheetTheme = false
                                        themeViewModel.setThemeMode(item.first)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Sheet content
        if (openBottomSheetLanguage) {
            ModalBottomSheet(
                onDismissRequest = {
                    openBottomSheetLanguage = false
                },
                sheetState = bottomSheetStateLanguage,
            ) {
                LazyColumn {
                    items(languageItems) { item ->
                        BottomSheetItem(
                            text = stringResource(id = item.second),
                            isSelected = item.first == language,
                            onClick = {
                                scope.launch {
                                    bottomSheetStateTheme.hide()
                                }.invokeOnCompletion {
                                    if (!bottomSheetStateTheme.isVisible) {
                                        openBottomSheetLanguage = false
                                        themeViewModel.setLanguage(item.first)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BottomSheetItem(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val color =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.align(Alignment.Center),
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "语言",
                tint = color,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    confirmClick: () -> Unit,
    cancelClick: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onDismissRequest()
        },
        title = {
            Text(
                text = stringResource(id = R.string.string_logout_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.string_logout_content),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirmClick()
                }
            ) {
                Text(stringResource(id = R.string.string_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    cancelClick()
                }
            ) {
                Text(stringResource(id = R.string.string_cancel))
            }
        }
    )
}


@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    item: Pair<Int, Int>,
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
            text = stringResource(id = item.second),
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

private val themeItems = listOf(
    Pair(0, R.string.string_follow_system),
    Pair(Configuration.UI_MODE_NIGHT_NO, R.string.string_dark_theme),
    Pair(Configuration.UI_MODE_NIGHT_YES, R.string.string_light_theme),
)

private val languageItems = listOf(
    Pair("system", R.string.string_follow_system),
    Pair("zh", R.string.string_zh),
    Pair("en", R.string.string_en),
    Pair("de", R.string.string_de),
    Pair("th", R.string.string_th),
)

@Preview(
    showBackground = true,
)
@Composable
fun SettingItemPreview(modifier: Modifier = Modifier) {
    SettingItem(
        modifier = modifier,
        item = Pair(0, R.string.string_settings),
        onClick = {},
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun BottomSheetItemPreview(modifier: Modifier = Modifier) {
    BottomSheetItem(
        modifier = modifier,
        isSelected = true,
        onClick = {},
        text = stringResource(id = R.string.string_follow_system),
    )
}