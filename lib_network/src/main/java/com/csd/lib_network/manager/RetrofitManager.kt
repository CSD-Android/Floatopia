package com.csd.lib_network.manager

import com.csd.lib_framework.helper.AppHelper
import com.csd.lib_network.error.ERROR
import com.csd.lib_network.error.NoNetworkException
import com.csd.lib_network.interceptor.AppendHeadersInterceptor
import com.sll.lib_network.util.NetWorkUtils
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


/**
 * 负责管理 retrofit 的 client 初始化，以及 ApiService 的动态代理
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/17
 */
object RetrofitManager {

    private val mCaches = hashMapOf<String, Retrofit>()

    /**
     * 获取实例
     * @param baseUrl
     * @param initializer 初始化 Retrofit 的操作，无需调用[baseUrl]
     * @return 实例化后的 Retrofit
     * */
    fun buildInstance(
        baseUrl: String,
        initializer: Retrofit.Builder.() -> Retrofit.Builder,
    ): Retrofit {
        if (mCaches.containsKey(baseUrl)) {
            return mCaches[baseUrl]!!
        } else {
            val instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .initializer()
                .build()
            mCaches[baseUrl] = instance
            return instance
        }
    }

    /**
     * 动态代理
     * @param retrofit 与 apiService 相匹配的
     * @param apiService 需要代理的类
     * */
    fun <T> create(retrofit: Retrofit, apiService: Class<T>): T {
        return retrofit.create(apiService)
    }

    /**
     * 配置 client
     * */
    fun initOkHttpClient(headers: Headers? = null): OkHttpClient {
        val build = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        // 请求头
        if (headers != null) {
            build.addInterceptor(AppendHeadersInterceptor(headers))
        }

        //日志拦截器
        val logInterceptor = HttpLoggingInterceptor()

        if (AppHelper.isDebug) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        }
        build.addInterceptor(logInterceptor)


        // 网络状态拦截
        build.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (NetWorkUtils.isNetworkAvailable(AppHelper.application)) {
                    val request = chain.request()
                    return chain.proceed(request)
                } else {
                    throw NoNetworkException(ERROR.NETWORK_ERROR)
                }
            }
        })
        return build.build()
    }

}