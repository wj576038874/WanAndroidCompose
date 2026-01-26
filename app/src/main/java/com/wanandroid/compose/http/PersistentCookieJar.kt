package com.wanandroid.compose.http

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.Date

/**
 * Created by wenjie on 2026/01/26.
 */
class PersistentCookieJar(private val context: Context) : CookieJar {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("wanandroid_cookies", Context.MODE_PRIVATE)
    }

    private val cookieCache = mutableMapOf<String, List<Cookie>>()


    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host

        // 先检查内存
        cookieCache[host]?.let { cached ->
            val now = Date().time
            val valid = cached.filter { it.expiresAt > now }  // 过滤过期
            if (valid.isNotEmpty()) return valid
        }
        // 从 SharedPreferences 加载
        val loadedCookies = mutableListOf<Cookie>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith("${host}_") && value is String) {
                Cookie.parse(url, value)?.let { cookie ->
                    if (cookie.expiresAt > Date().time) {  // 再次过滤过期
                        loadedCookies.add(cookie)
                    } else {
                        // 清理过期
                        prefs.edit().remove(key).apply()
                    }
                }
            }
        }
        // 更新内存
        cookieCache[host] = loadedCookies

        return loadedCookies

    }

    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>
    ) {
        val host = url.host
        val validCookies = cookies.filter { it.persistent }  // 只存持久化的（有 expiresAt）
        cookieCache[host] = validCookies
        // 持久化到 SharedPreferences
        val editor = prefs.edit()
        validCookies.forEach { cookie ->
            val key = "${host}_${cookie.name}"
            editor.putString(key, cookie.toString())  // Cookie.toString() 是标准 HTTP 格式
        }
        editor.apply()
    }

    // 退出登录或清空时调用
    fun clear() {
        cookieCache.clear()
        prefs.edit().clear().apply()
    }
}