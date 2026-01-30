package com.wanandroid.compose.module

import com.wanandroid.compose.coin.CoinRepository
import com.wanandroid.compose.coin.NetworkCoinRepository
import com.wanandroid.compose.collect.CollectRepository
import com.wanandroid.compose.collect.NetworkCollectRepository
import com.wanandroid.compose.main.repository.impl.NetworkHomeRepository
import com.wanandroid.compose.main.repository.HomeRepository
import com.wanandroid.compose.main.repository.NavigationRepository
import com.wanandroid.compose.main.repository.impl.NetworkNavigationRepository
import com.wanandroid.compose.main.repository.impl.NetworkQuestionAnswerRepository
import com.wanandroid.compose.main.repository.QuestionAnswerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by wenjie on 2026/01/30.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(homeRepository: NetworkHomeRepository): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCollectRepository(collectRepository: NetworkCollectRepository): CollectRepository

    @Binds
    @Singleton
    abstract fun bindCoinRepository(coinRepository: NetworkCoinRepository): CoinRepository

    @Binds
    @Singleton
    abstract fun bindQuestionAnswerRepository(questionAnswerRepository: NetworkQuestionAnswerRepository): QuestionAnswerRepository

    @Binds
    @Singleton
    abstract fun bindNavigationRepository(navigationRepository: NetworkNavigationRepository): NavigationRepository
}