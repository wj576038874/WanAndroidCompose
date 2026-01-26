package com.wanandroid.compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wanandroid.compose.AuthViewModel
import com.wanandroid.compose.ui.theme.WanAndroidComposeTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.checkLogin()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !authViewModel.isReady.value
            }
            setOnExitAnimationListener {
                it.remove()
            }
        }
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Force the 3-button navigation bar to be transparent
            // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            WanAndroidComposeTheme {
                WanAndroidApp(authViewModel = authViewModel)
            }
        }
    }
}

