package com.wanandroid.compose

import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Created by wenjie on 2026/01/26.
 */
class AppViewModel : ViewModel() {

    private val sp by lazy {
        WanAndroidApplication.context.getSharedPreferences("MY_THEME", MODE_PRIVATE)
    }

    val defaultLanguage = Locale.getDefault().language

    init {
        Log.e("asd", defaultLanguage)
    }

    /**
     * 主题模式
     */
    private val _themeMode = MutableStateFlow(
        sp.getInt("THEME_MODE", Configuration.UI_MODE_NIGHT_YES)
    )
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    /**
     * 设置主题模式
     */
    fun setThemeMode(themeMode: Int) {
        _themeMode.value = themeMode
        sp.edit {
            putInt("THEME_MODE", themeMode)
        }
    }



    private val currentLanguage = if (AppCompatDelegate.getApplicationLocales().isEmpty) {
        // 如果没有设置应用语言，则使用系统默认语言
        "system" //用来作为跟随系统语言的标志 设置的时候进行判断，如果选择system 则设置空集合代表跟随系统语言
    } else {
        // 获取应用设置的语言
        AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: ""
    }
    private val _language = MutableStateFlow(currentLanguage)
    val language: StateFlow<String> = _language.asStateFlow()

    /**
     *
     * 首次切换会闪烁
     * 如果没有设置过AppCompatDelegate.setApplicationLocales，则跟随系统语言，
     * 假设系统当前是zh中文，那么第一次打开应用时，
     * Locale.getDefault().toLanguageTag() = zh 会返回系统当前语言的语言标签，
     * resources.getSystem().configuration.locales[0].toLanguageTag()=zh 也会返回系统当前语言的语言标签，
     * AppCompatDelegate.getApplicationLocales()会返回空列表
     * AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() = null
     *
     * 如果设置过AppCompatDelegate.setApplicationLocales，则会返回设置的语言标签，假设设置为en英文，那么
     * 那么 AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() = en
     * resources.getSystem().configuration.locales[0].toLanguageTag() = en
     * Locale.getDefault().toLanguageTag() = en
     *
     * //下面三种都是可以获取当前应用设置的语言
     *         Log.e("asd0", currentLanguage)
     *         //当前应用设置的语言 第一次打开系统是什么语言就获取的是什么语言 如果设置了则返回设置的语言标签，否则返回系统默认语言标签
     *         Log.e("asd1", Locale.getDefault().toLanguageTag())
     *         //当前应用设置的语言 第一次打开系统是什么语言就获取的是什么语言 如果设置了则返回设置的语言标签，否则返回系统默认语言标签
     *         Log.e("asd2", Resources.getSystem().configuration.locales[0].toLanguageTag())
     *         //当前应用设置的语言 如果没有设置过AppCompatDelegate.setApplicationLocales，则返回null
     *         Log.e("asd3", AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag().toString())
     *
     */
    fun setLanguage(language: String) {
        _language.value = language
        if (language == "system") {
            // 如果选择了系统语言，则使用系统默认语言 设置空集合即可
            // 设置之后AppCompatDelegate.getApplicationLocales() 会返回空列表
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.getEmptyLocaleList()
            )
//            WanAndroidApplication.context.getSystemService(LocaleManager::class.java)
//                .applicationLocales = LocaleList.getEmptyLocaleList()
        } else {
            //否则直接使用选择的语言
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(
                    language
                )
            )
//            WanAndroidApplication.context.getSystemService(LocaleManager::class.java)
//                .applicationLocales = LocaleList.forLanguageTags(language)
        }
    }


    /**
     * 监听语言变化，更新应用语言
     * 这种方式 dialog和bottomsheet 会使用系统默认语言 因为它们是独立的窗口
     * 拿到的不是不是新的context（LocalContext） 所以dialog和bottomsheet 会使用系统默认语言
     *
     *     val language2 by appViewModel.language2.collectAsStateWithLifecycle()
     *     val configuration = LocalConfiguration.current
     *     if (language2 == "system"){
     *         val newLocale = Locale.forLanguageTag(appViewModel.defaultLanguage)
     *         Locale.setDefault(newLocale)
     *         configuration.setLocale(newLocale)
     *     }else{
     *         val newLocale = Locale.forLanguageTag(language2)
     *         Locale.setDefault(newLocale)
     *         configuration.setLocale(newLocale)
     *     }
     *     val context = LocalContext.current
     *     val newContext = context.createConfigurationContext(configuration)
     *
     *     CompositionLocalProvider(
     *             LocalContext provides newContext,
     *         )
     */
    private val _language2 = MutableStateFlow(
        sp.getString("LANGUAGE", defaultLanguage) ?: defaultLanguage
    )
    val language2: StateFlow<String> = _language2.asStateFlow()

    fun setLanguage2(language: String) {
        _language2.value = language
        sp.edit {
            putString("LANGUAGE", language)
        }
    }

}