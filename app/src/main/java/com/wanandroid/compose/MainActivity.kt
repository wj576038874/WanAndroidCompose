package com.wanandroid.compose

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.compose.locale.AppLocale
import com.wanandroid.compose.ui.theme.WanAndroidComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appViewModel: AppViewModel by viewModels()

//    private val splashViewModel: SplashViewModel by viewModels {
//        val loginApi = RetrofitHelper.create(LoginApi::class.java)
//        val loginRepository = LoginRepository(loginApi)
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                @Suppress("UNCHECKED_CAST") return SplashViewModel(loginRepository) as T
//            }
//        }
//    }
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val zhAppLocale = AppLocale("zh")
        val home = zhAppLocale.getString(R.string.string_home)
        val enAppLocale = AppLocale("en")
        val homeEn = enAppLocale.getString(R.string.string_home)
        val thAppLocale = AppLocale("th")
        val homeTh = thAppLocale.getString(R.string.string_home)
        val deAppLocale = AppLocale("de")
        val homeDe = deAppLocale.getString(R.string.string_home)
        Log.e("AppLocale", "onCreate: ${Locale.getDefault().language} $home $homeEn $homeTh $homeDe")
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
                darkTheme = isDarkTheme,
                dynamicColor = false
            ) {
                WanAndroidApp(appViewModel = appViewModel)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e("MainActivity", "onConfigurationChanged: ${newConfig.locales} ${Locale.getDefault().language}")
        AppCompatDelegate.setApplicationLocales(
            AppCompatDelegate.getApplicationLocales() // 触发内部刷新
        )
        appViewModel.updateAppLocale(newConfig.locales.get(0).language)
    }
}

