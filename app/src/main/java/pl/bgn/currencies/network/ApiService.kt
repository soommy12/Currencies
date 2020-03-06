package pl.bgn.currencies.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Protocol
import pl.bgn.currencies.data.Model
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest")
    fun getLatestCurrencyRate(@Query("base") base: String): Observable<Model.Base>

    companion object {
        fun create() : ApiService {
            val client = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1)).build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://hiring.revolut.codes/api/android/")
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}