package com.wanandroid.compose.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.R
import com.wanandroid.compose.WanAndroidApplication
import com.wanandroid.compose.common.LoadingDialog
import com.wanandroid.compose.login.state.LoginState

/**
 * Created by wenjie on 2026/01/26.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onLogin: () -> Unit,
) {
    val loginViewModel = hiltViewModel<LoginViewModel>()
    var userName by remember { mutableStateOf("wj576038874") }
    var password by remember { mutableStateOf("1rujiwang") }
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onBackClick()
            onLogin()
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        when (loginState) {
            is LoginState.Loading -> {
                LoadingDialog(
                    onDismissRequest = {
                        loginViewModel.cancelLogin()
                    }
                )
            }

            is LoginState.Failure -> {
                Toast.makeText(
                    WanAndroidApplication.context,
                    (loginState as LoginState.Failure).errorMsg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.string_welcome),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(Modifier.height(64.dp))
            TextField(
                value = userName,
                onValueChange = {
                    userName = it
                },
                label = {
                    Text(
                        text = stringResource(R.string.string_username),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(
                        text = stringResource(R.string.string_password),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp
                    )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    loginViewModel.login(userName, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp
                    )
            ) {
                Text(
                    text = stringResource(R.string.string_login),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

//@Composable
//fun Sector(
//    startAngle: Float,         // 起始角度（度）
//    sweepAngle: Float,         // 扇形角度（度）
//    color: Color,
//) {
//    Canvas(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//    ) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val radius = minOf(canvasWidth, canvasHeight) / 2f
//        val center = Offset(canvasWidth / 6f, canvasHeight / 2f)
//
//        // 画扇形（最核心的一行）
//        drawArc(
//            color = color,
//            startAngle = startAngle,
//            sweepAngle = sweepAngle,
//            useCenter = true,           // true = 从圆心开始画扇形
//            topLeft = Offset(center.x - radius, center.y - radius),
//            size = Size(radius * 3, radius * 2)
//        )
//    }
//}