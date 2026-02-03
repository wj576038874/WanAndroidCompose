package com.wanandroid.compose.main.viemodel

import androidx.lifecycle.ViewModel
import com.wanandroid.compose.login.event.ProfileEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Created by wenjie on 2026/02/02.
 */
class ProfileViewModel : ViewModel() {

    private val _channel = Channel<ProfileEvent>()
    val event = _channel.receiveAsFlow()

}