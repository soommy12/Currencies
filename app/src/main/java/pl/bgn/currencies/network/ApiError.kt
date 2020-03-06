package pl.bgn.currencies.network

import android.util.Log
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ApiError(error: Throwable){

    private val TAG = "ApiError"
    var msg = "An unexpected error occurred"

    init{
        when (error) {
            // if any known error from api occured it can be handler here
            is HttpException -> {
                val errBodyStr = error.response()?.errorBody()?.string()
                errBodyStr?.let {
                    this.msg = "Error from API"
                }
            }
            is SocketTimeoutException -> this.msg = "The server is under maintenance"
            is UnknownHostException -> this.msg = "No Internet connection"
            else -> Log.e("ApiError", error.toString())

        }
    }
}