package com.wanandroid.compose.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wenjie on 2026/01/22.
 */
data class BaseResponse<T>(
    @SerializedName("data") val data: T? = null,
    @SerializedName("errorMsg") val message: String? = null,
    @SerializedName("errorCode") val code: Int = 0
) {
    val isSuccess: Boolean
        get() = code == 0
}
