package pl.bgn.currencies.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
    val shouldDisplayToast = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    init {
        responder.value = Model.Currency("EUR", 10.0)
        shouldDisplayToast.value = false
        startInterval()
    }

    fun startInterval() {
        disposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .subscribe(
                { getCurrencies() },
                { error ->
                    run {
                        Log.e("Currencies", "Problem from interval: ${error.localizedMessage}")
                        disposable?.dispose()
                    }
                })
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
                        shouldDisplayToast.value = true
                        errorMessage.value = ApiError(error).msg
                        Log.e("Currencies", "Problem: $error")
                    }
                }
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
        shouldDisplayToast.value = false
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }

    fun onResponderChange(position: Int) {
        if(position != 0) {
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

    fun getCurrencyUniqueId(position: Int) = currenciesData.value?.get(position)!!.id

    fun stopFetch() {
        disposable?.dispose()
//        disposables.clear()
    }
}