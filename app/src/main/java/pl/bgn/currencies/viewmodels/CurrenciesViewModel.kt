package pl.bgn.currencies.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.network.ApiService
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CurrenciesViewModel : ViewModel() {

    private val apiService by lazy { ApiService.create() }
    val currenciesData: MutableLiveData<List<Model.Currency>> = MutableLiveData()
    var responder: MutableLiveData<Model.Currency> = MutableLiveData()
    private val currenciesList: ArrayList<Model.Currency> = ArrayList()
    private var disposable: Disposable? = null

    init {
        responder.value = Model.Currency("EUR", 10.0)
        startInterval()
    }

    private fun startInterval() {
        disposable = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getCurrencies() },
                { error -> Log.e("Currencies", "Problem: ${error.message}") })
    }

    private fun getCurrencies() {

        val observable: Observable<Model.Base> =
            apiService.getLatestCurrencyRate(responder.value!!.name)
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> handleResult(result) },
                { error -> Log.e("Currencies", "Problem: ${error.message}") })
    }

    private fun handleResult(result: Model.Base) {
        val rates = result.rates
        if(currenciesList.isEmpty()) {
            currenciesList.add(responder.value!!)
            for((k,v) in rates) currenciesList.add(Model.Currency(k, v))
        } else for(i in 1 until currenciesList.size)
            currenciesList[i].rate = rates[currenciesList[i].name]!! //todo avoid nulls here
        currenciesData.value = emptyList()
        currenciesData.value = currenciesList
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

    fun onResponderChange(position: Int) {
        disposable?.dispose()
        val current = currenciesList[position]
        val newResponder
                = Model.Currency(current.name, current.rate * responder.value!!.rate)
        currenciesList.removeAt(position)
        currenciesList.add(0, newResponder)
        responder.value = newResponder
        startInterval()
    }
}