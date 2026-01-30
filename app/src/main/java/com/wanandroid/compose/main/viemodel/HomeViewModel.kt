package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.compose.UserManager
import com.wanandroid.compose.bean.BannerItem
import com.wanandroid.compose.main.repository.HomeRepository
import com.wanandroid.compose.main.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by wenjie on 2026/01/22.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            UserManager.instance.userInfo.collect { userInfo ->
                _homeUiState.value = _homeUiState.value.copy(
                    articleList = _homeUiState.value.articleList?.map { article ->
                        val isCollected = userInfo?.collectIds?.contains(article.id) ?: false
                        article.copy(collect = isCollected)
                    }
                )
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
            homeRepository.getBannerList().onSuccess {
                bannerList = it
            }
            homeRepository.getArticleList(pageNum).apply {
                onSuccess {
                    _homeUiState.value = _homeUiState.value.copy(
                        bannerList = bannerList,
                        articleList = it.datas,
                        noMoreData = false,
                        isLoading = false
                    )
                }
                onFailure {
                    _homeUiState.value = _homeUiState.value.copy(
                        errorMsg = it.message ?: "获取首页数据失败", isLoading = false
                    )
                }
            }
        }
    }

    fun loadNexPage(pageNum: Int) {
        viewModelScope.launch {
            homeRepository.getArticleList(pageNum).apply {
                onSuccess {
                    val newData = it.datas ?: emptyList()
                    if (newData.isEmpty()) {
                        _homeUiState.value.copy(
                            noMoreData = true, isLoading = false
                        )
                    } else {
                        val oldData = _homeUiState.value.articleList ?: emptyList()
                        _homeUiState.value = _homeUiState.value.copy(
                            articleList = oldData + newData, isLoading = false, noMoreData = false
                        )
                    }
                }
                onFailure {
                    _homeUiState.value = _homeUiState.value.copy(
                        errorMsg = it.message ?: "获取下一页数据失败", isLoading = false
                    )
                }
            }
        }
    }

    fun collectArticle(id: Int) {
        viewModelScope.launch {
            homeRepository.collectArticle(id).apply {
                onSuccess {
                    _homeUiState.value = _homeUiState.value.copy(
                        articleList = _homeUiState.value.articleList?.map {
                            if (it.id == id) {
                                it.copy(collect = true)
                            } else {
                                it
                            }
                        })
                    UserManager.instance.addCollectId(id)
                }
                onFailure {
                    _homeUiState.value = _homeUiState.value.copy(
                        errorMsg = it.message ?: "收藏文章失败", isLoading = false
                    )
                }
            }
        }
    }

    fun unCollectArticle(id: Int) {
        viewModelScope.launch {
            homeRepository.unCollectArticle(id).apply {
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
                    _homeUiState.value = _homeUiState.value.copy(
                        errorMsg = it.message ?: "取消收藏文章失败", isLoading = false
                    )
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