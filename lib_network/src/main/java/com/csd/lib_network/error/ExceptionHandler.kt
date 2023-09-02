package com.csd.lib_network.error

import com.csd.lib_network.constant.CODE_RESOURCE_NOT_FOUND
import com.csd.lib_network.constant.CODE_SERVER_ERROR
import com.csd.lib_network.constant.CODE_UNAUTHORIZED
import com.csd.lib_network.constant.CODE_UNKNOWN
import com.csd.lib_network.constant.CODE_UNLOGIN
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
object ExceptionHandler {

    fun handle(e: Throwable): ApiException {
        val exception: ApiException =
        when(e) {
            is ApiException -> {
                if (e.code == CODE_UNLOGIN) {
                    // TODO： 未登录措施
                }
                ApiException(e.code, e.msg, e)
            }
            is NoNetworkException -> {
                // TODO: 提示没有网络
                ApiException(ERROR.NETWORK_ERROR, e)
            }
            is HttpException -> {
                when(e.code()) {
                    CODE_UNAUTHORIZED -> ApiException(ERROR.UNAUTHORIZED, e)
                    CODE_RESOURCE_NOT_FOUND -> ApiException(ERROR.NOT_FOUND, e)
                    CODE_SERVER_ERROR -> ApiException(ERROR.SERVER_ERROR, e)
                    else -> ApiException(e.code(), e.message(), e)
                }
            }
            is JSONException,
            is JsonDataException,
            is JsonEncodingException -> ApiException(ERROR.PARSE_ERROR, e)
            is ConnectException -> ApiException(ERROR.NETWORK_ERROR, e)
            is SSLException -> ApiException(ERROR.SSL_ERROR, e)
            is SocketException,
            is SocketTimeoutException -> ApiException(ERROR.TIMEOUT_ERROR, e)
            is UnknownHostException -> ApiException(ERROR.UNKNOWN_HOST, e)
            else -> {
                e.message
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { ApiException(CODE_UNKNOWN, it, e) }
                    ?: ApiException(ERROR.UNKNOWN, e)
            }
        }
        return exception
    }
}