package com.wanandroid.compose

/**
 * Created by wenjie on 2026/01/26.
 */
class UserManager private constructor(){
    companion object {
        val instance: UserManager by lazy { UserManager() }
    }


}