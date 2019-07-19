package pl.bgn.currencies.network

import io.reactivex.Observable
import pl.bgn.currencies.Model
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest")
    fun getLatestCurrencyRate(@Query("base") base: String): Observable<Model.Base>
//    fun getLatestCurrencyRate(): Observable<Model.Base>

    companion object {
        fun create() : ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://revolut.duckdns.org/")
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}