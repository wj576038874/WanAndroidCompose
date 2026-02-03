package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.main.event.HomeEvent
import com.wanandroid.compose.main.repository.impl.NetworkHomeRepository
import com.wanandroid.compose.main.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by wenjie on 2026/01/22.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val networkHomeRepository: NetworkHomeRepository) :
    ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _homeChannel = Channel<HomeEvent>()
    val homeChannel = _homeChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            UserManager.instance.userInfo.collect { userInfo ->
                _homeUiState.update { state ->
                    state.copy(
                        articleList = _homeUiState.value.articleList?.map { article ->
                            val isCollected = userInfo?.collectIds?.contains(article.id) ?: false
                            article.copy(collect = isCollected)
                        }
                    )
                }
            }
        }
        getHomeData(0)
    }

    fun getHomeData(pageNum: Int) {
        viewModelScope.launch {
            _homeUiState.update {
                it.copy(isLoading = true)
            }
            var bannerList: List<BannerItem>? = null
            networkHomeRepository.getBannerList().onSuccess {
                bannerList = it
            }
            networkHomeRepository.getArticleList(pageNum).apply {
                onSuccess {
                    _homeUiState.update { state ->
                        state.copy(
                            bannerList = bannerList,
                            articleList = it.datas,
                            noMoreData = false,
                            isLoading = false
                        )
                    }
                }
                onFailure {
                    _homeUiState.update { state ->
                        state.copy(isLoading = false)
                    }
                    _homeChannel.send(HomeEvent.Error(it.message ?: "获取首页数据失败"))
                }
            }
        }
    }

    fun loadNexPage(pageNum: Int) {
        viewModelScope.launch {
            networkHomeRepository.getArticleList(pageNum).apply {
                onSuccess {
                    val newData = it.datas ?: emptyList()
                    if (newData.isEmpty()) {
                        _homeUiState.update { state ->
                            state.copy(
                                noMoreData = true, isLoading = false
                            )
                        }
                    } else {
                        val oldData = _homeUiState.value.articleList ?: emptyList()
                        _homeUiState.update { state ->
                            state.copy(
                                articleList = oldData + newData,
                                isLoading = false,
                                noMoreData = false
                            )
                        }
                    }
                }
                onFailure {
                    _homeUiState.update { state ->
                        state.copy(
                            nextPageError = it.message ?: "获取下一页数据失败", isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun collectArticle(id: Int) {
        viewModelScope.launch {
            networkHomeRepository.collectArticle(id).apply {
                onSuccess {
                    _homeUiState.update { state ->
                        state.copy(
                            articleList = _homeUiState.value.articleList?.map {
                                if (it.id == id) {
                                    it.copy(collect = true)
                                } else {
                                    it
                                }
                            })
                    }
                    UserManager.instance.addCollectId(id)
                }
                onFailure {
                    _homeChannel.send(HomeEvent.Error(it.message ?: "收藏文章失败"))
                }
            }
        }
    }

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            networkHomeRepository.unCollectArticle(id).apply {
                onSuccess {
                    _homeUiState.value = _homeUiState.value.copy(
                        articleList = _homeUiState.value.articleList?.map {
                            if (it.id == id) {
                                it.copy(collect = false)
                            } else {
                                it
                            }
                        })
                    UserManager.instance.removeCollectId(id)
                }
                onFailure {
                    _homeChannel.send(HomeEvent.Error(it.message ?: "取消收藏文章失败"))
                }
            }
        }
    }

//    fun getBannerList() {
//        _homeUiState.value = _homeUiState.value.copy(isLoading = true)
//        viewModelScope.launch {
//            runCatching {
//                delay(2000)
//                homeRepository.getBannerList()
//            }.onSuccess {
//                _homeUiState.value =
//                    _homeUiState.value.copy(bannerList = it.data, isLoading = false)
//            }.onFailure {
//                _homeUiState.value = _homeUiState.value.copy(
//                    errorMsg = it.message ?: "获取banner列表失败", isLoading = false
//                )
//            }
//        }
//    }
//
//    fun getArticleList(pageNum: Int) {
////        _homeUiState.value = _homeUiState.value.copy(isLoading = true)
//        viewModelScope.launch {
//            runCatching {
//                delay(2000)
//                homeRepository.getArticleList(pageNum).data?.datas
//            }.onSuccess {
//                _homeUiState.value = _homeUiState.value.copy(articleList = it, isLoading = false)
//            }.onFailure {
//                _homeUiState.value = _homeUiState.value.copy(
//                    errorMsg = it.message ?: "获取文章列表失败", isLoading = false
//                )
//            }
//        }
//    }
}