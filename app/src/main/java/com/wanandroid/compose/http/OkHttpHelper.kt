package com.wanandroid.compose.http

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by wenjie on 2025/05/28.
 */
class OkHttpHelper private constructor() {

    private val mHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
//            .addInterceptor(RequestSignInterceptor())
//        if (BuildConfig.DEBUG) {
//            builder.addInterceptor(CurlInterceptor {
//                Log.e("request", it)
//            })
//            builder.addInterceptor(LoggerInterceptor("request"))
//        }
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            builder.socketFactory(sslSocketFactory)
        } catch (e: Exception) {
            //
        }
        mHttpClient = builder.build()
    }

    fun getOkHttpClient(): OkHttpClient {
        return mHttpClient
    }

    companion object {
        private const val TIMEOUT = 30000

        @JvmStatic
        val instance by lazy {
            OkHttpHelper()
        }
    }
}