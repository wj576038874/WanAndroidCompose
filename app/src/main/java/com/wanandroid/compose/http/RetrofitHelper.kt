package com.wanandroid.compose.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by wenjie on 2025/05/28.
 */
class RetrofitHelper {

    companion object {
        @Volatile
        private var retrofit: Retrofit? = null

        /**
         * 方案一 目前后端说只有一个baseUrl 用这个比较好 全局只生成一个retrofit对象
         */
        private fun getRetrofit() = retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
                .client(OkHttpHelper.instance.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create()).build().also {
                    retrofit = it
                }
        }

        @JvmStatic
        fun <T> create(clazz: Class<T>): T {
            return getRetrofit().create(clazz)
        }

        /**
         * debug情况下切换环境使用重置单例
         */
        fun reset() {
            retrofit = null
        }

    }
}