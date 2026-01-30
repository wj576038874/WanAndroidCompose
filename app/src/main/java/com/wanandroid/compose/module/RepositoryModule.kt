package com.wanandroid.compose.module

import com.wanandroid.compose.main.repository.HomeRepository
import com.wanandroid.compose.main.repository.IHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by wenjie on 2026/01/30.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindsHomeRepository(homeRepository: HomeRepository): IHomeRepository

//    @Binds
//    abstract fun bindsCollectRepository(collectRepository: CollectRepository): ICollectRepository
}