package com.wanandroid.compose.module

import com.wanandroid.compose.coin.CoinApi
import com.wanandroid.compose.collect.CollectApi
import com.wanandroid.compose.http.RetrofitHelper
import com.wanandroid.compose.login.LoginApi
import com.wanandroid.compose.main.api.HomeApi
import com.wanandroid.compose.main.api.NavigationApi
import com.wanandroid.compose.main.api.QuestionAnswerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by wenjie on 2026/01/30.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeApi(): HomeApi {
        return RetrofitHelper.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCollectApi(): CollectApi {
        return RetrofitHelper.create(CollectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinApi(): CoinApi {
        return RetrofitHelper.create(CoinApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionAnswerApi(): QuestionAnswerApi {
        return RetrofitHelper.create(QuestionAnswerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNavigationApi(): NavigationApi {
        return RetrofitHelper.create(NavigationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(): LoginApi {
        return RetrofitHelper.create(LoginApi::class.java)
    }
}