package pl.bgn.currencies.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.bgn.currencies.data.ConnectionLiveData
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.network.ApiError
import pl.bgn.currencies.network.ApiService
import java.util.concurrent.TimeUnit


class CurrenciesViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService by lazy { ApiService.create() }
    val currenciesData: MutableLiveData<List<Model.Currency>> = MutableLiveData()
    val responder: MutableLiveData<Model.Currency> = MutableLiveData()
    val connectionLiveData = ConnectionLiveData(application)
    private val currenciesList: ArrayList<Model.Currency> = ArrayList()
    private var disposable: Disposable? = null
    private val disposables = CompositeDisposable()
    var currenciesVisible: Boolean = false

    init {
        responder.value = Model.Currency("EUR", 10.0)
    }

    fun startInterval() {
        if(connectionLiveData.value == true) {
            disposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(
                    { getCurrencies() },
                    { error -> Log.e("Currencies", "Problem from interval: ${error.localizedMessage}") })
        }
    }

    private fun getCurrencies() {

        val observable: Observable<Model.Base> =
            apiService.getLatestCurrencyRate(responder.value!!.name)
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> handleResult(result) },
                { error ->
                    run {
//                        errorMessage.value = ApiError(error).msg
//                        currenciesVisible.value = false
                        currenciesVisible = false
                        Log.e("Currencies", "Problem: $error")
                    }
                },
                {},
                { d -> disposables.add(d) }
            )
    }

    private fun handleResult(result: Model.Base) {
        val rates = result.rates
        if(currenciesList.isEmpty()) {
            currenciesList.add(responder.value!!)
            for((k,v) in rates) currenciesList.add(Model.Currency(k, v))
        } else for(i in 1 until currenciesList.size)
            currenciesList[i].rate = rates.getValue(currenciesList[i].name)
        currenciesData.value = null // to avoid double observer calls
        currenciesData.value = currenciesList
        currenciesVisible = true
    }

    override fun onCleared() {
        stopFetch()
        super.onCleared()
    }

    fun onResponderChange(position: Int, value: String) {
        if(position != 0) {
            stopFetch()
            val current = currenciesList[position]
            val newResponder
                    = Model.Currency(current.name, value.toDouble())
            currenciesList.removeAt(position)
            currenciesList.add(0, newResponder)
            responder.value = newResponder
            startInterval()
        }
    }

    fun getCurrencyUniqueId(position: Int) = currenciesData.value?.get(position)!!.id

    fun stopFetch() {
        println("stopFetch()")
        disposables.clear()
        disposable?.dispose()
    }
}