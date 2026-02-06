package com.wanandroid.compose.login

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.R
import com.wanandroid.compose.login.action.LoginAction
import com.wanandroid.compose.login.event.LoginEvent
import com.wanandroid.compose.utils.ObserveAsEvents

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
    val viewModel = hiltViewModel<LoginViewModel>()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val usernameFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    ObserveAsEvents(
        flow = viewModel.loginEvent,
        onEvent = { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> {
                    onBackClick()
                    onLogin()
                }

                is LoginEvent.LoginFailure -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMsg,
                    )
                }
            }
        })

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
//        if (loginState.isLoading) {
//            LoadingDialog(
//                onDismissRequest = {
//                    loginViewModel.cancelLogin()
//                }
//            )
//        }

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
                value = loginState.userName,
                onValueChange = {
                    viewModel.onAction(LoginAction.InputUserName(it))
                },
//                isError = !loginState.isUserNameValid,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        passwordFocus.requestFocus()
                    }
                ),
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
                    errorContainerColor = Color.Transparent,
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp
                    )
                    .focusRequester(usernameFocus)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = loginState.password,
                onValueChange = {
                    viewModel.onAction(LoginAction.InputPassword(it))
                },
//                isError = !loginState.isPasswordValid,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (loginState.canLogin) {
                            viewModel.onAction(LoginAction.Login)
                        }
                    }
                ),
                label = {
                    Text(
                        text = stringResource(R.string.string_password),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (loginState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onAction(LoginAction.UpdateIsPasswordVisible(!loginState.isPasswordVisible))
                        }
                    ) {
                        Icon(
                            imageVector = if (loginState.isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                    errorContainerColor = Color.Transparent,
                ),
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 32.dp
                    )
                    .focusRequester(passwordFocus)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.onAction(LoginAction.Login)
                },
                enabled = loginState.canLogin && !loginState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(
                        horizontal = 32.dp
                    )
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.string_login),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
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