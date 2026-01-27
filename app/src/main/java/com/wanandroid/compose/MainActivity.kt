package com.wanandroid.compose

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.http.LoginApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.login.LoginRepository
import com.wanandroid.compose.ui.theme.WanAndroidComposeTheme

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    private val splashViewModel: SplashViewModel by viewModels {
        val loginApi = RetrofitHelper.create(LoginApi::class.java)
        val loginRepository = LoginRepository(loginApi)
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST") return SplashViewModel(loginRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.checkLogin()
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !splashViewModel.isReady.value
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
            val themeMode by appViewModel.themeMode.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                Configuration.UI_MODE_NIGHT_YES -> false
                Configuration.UI_MODE_NIGHT_NO -> true
                else -> isSystemInDarkTheme()
            }
            WanAndroidComposeTheme(
                darkTheme = isDarkTheme, dynamicColor = false
            ) {
                WanAndroidApp(appViewModel = appViewModel)
            }
        }
    }
}

