package com.wanandroid.compose.module

import android.util.Log
import com.wanandroid.compose.coin.CoinApi
import com.wanandroid.compose.collect.CollectApi
import com.wanandroid.compose.http.OkHttpHelper
import com.wanandroid.compose.login.LoginApi
import com.wanandroid.compose.main.api.HomeApi
import com.wanandroid.compose.main.api.NavigationApi
import com.wanandroid.compose.main.api.QuestionAnswerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by wenjie on 2026/01/30.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
            .client(OkHttpHelper.instance.getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideHomeApi(
        retrofit: Retrofit
    ): HomeApi {
        Log.e("AppModule", "provideHomeApi$retrofit")
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCollectApi(retrofit: Retrofit): CollectApi {
        Log.e("AppModule", "provideCollectApi$retrofit")
        return retrofit.create(CollectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinApi(retrofit: Retrofit): CoinApi {
        Log.e("AppModule", "provideCoinApi$retrofit")
        return retrofit.create(CoinApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionAnswerApi(retrofit: Retrofit): QuestionAnswerApi {
        Log.e("AppModule", "provideQuestionAnswerApi$retrofit")
        return retrofit.create(QuestionAnswerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNavigationApi(retrofit: Retrofit): NavigationApi {
        Log.e("AppModule", "provideNavigationApi$retrofit")
        return retrofit.create(NavigationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApi {
        Log.e("AppModule", "provideLoginApi$retrofit")
        return retrofit.create(LoginApi::class.java)
    }

}