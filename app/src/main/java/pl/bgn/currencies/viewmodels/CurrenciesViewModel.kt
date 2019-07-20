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
import java.util.concurrent.TimeUnit

class CurrenciesViewModel : ViewModel() {

    private val apiService by lazy { ApiService.create() }
    val currenciesData: MutableLiveData<List<Model.Currency>> = MutableLiveData()
    val responderCurrency: MutableLiveData<Model.Responder> = MutableLiveData()
    private val currenciesList: ArrayList<Model.Currency> = ArrayList()
    private var disposable: Disposable? = null

    init {
        responderCurrency.value = Model.Responder("EUR", 10.0)
        disposable = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getCurrencies() },
                { error -> Log.e("Currencies", "Problem: ${error.message}") })
    }

    private fun getCurrencies() {

        val observable: Observable<Model.Base> =
            apiService.getLatestCurrencyRate(responderCurrency.value!!.name)
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> handleResult(result) },
                { error -> Log.e("Currencies", "Problem: ${error.message}") })
    }

    private fun handleResult(result: Model.Base) {
        val rates = result.rates
        currenciesList.clear()
        currenciesList.add(Model.Currency("AUD", rates.AUD))
        currenciesList.add(Model.Currency("BGN", rates.BGN))
        currenciesList.add(Model.Currency("BRL", rates.BRL))
        currenciesList.add(Model.Currency("CAD", rates.CAD))
        currenciesList.add(Model.Currency("CHF", rates.CHF))
        currenciesList.add(Model.Currency("CNY", rates.CNY))
        currenciesList.add(Model.Currency("CZK", rates.CZK))
        currenciesList.add(Model.Currency("DKK", rates.DKK))
        currenciesList.add(Model.Currency("EUR", rates.EUR))
        currenciesList.add(Model.Currency("GBP", rates.GBP))
        currenciesList.add(Model.Currency("HKD", rates.HKD))
        currenciesList.add(Model.Currency("HRK", rates.HRK))
        currenciesList.add(Model.Currency("HUF", rates.HUF))
        currenciesList.add(Model.Currency("IDR", rates.IDR))
        currenciesList.add(Model.Currency("ILS", rates.ILS))
        currenciesList.add(Model.Currency("INR", rates.INR))
        currenciesList.add(Model.Currency("JPY", rates.JPY))
        currenciesList.add(Model.Currency("KRW", rates.KRW))
        currenciesList.add(Model.Currency("MXN", rates.MXN))
        currenciesList.add(Model.Currency("MYR", rates.MYR))
        currenciesList.add(Model.Currency("NOK", rates.NOK))
        currenciesList.add(Model.Currency("NZD", rates.NZD))
        currenciesList.add(Model.Currency("PHP", rates.PHP))
        currenciesList.add(Model.Currency("PLN", rates.PLN))
        currenciesList.add(Model.Currency("RON", rates.RON))
        currenciesList.add(Model.Currency("RUB", rates.RUB))
        currenciesList.add(Model.Currency("SEK", rates.SEK))
        currenciesList.add(Model.Currency("SGD", rates.SGD))
        currenciesList.add(Model.Currency("THB", rates.THB))
        currenciesList.add(Model.Currency("TRY", rates.TRY))
        currenciesList.add(Model.Currency("USD", rates.USD))
        currenciesList.add(Model.Currency("ZAR", rates.ZAR))
        for(i in 0 until currenciesList.size - 1)
            if(currenciesList[i].name == result.base) currenciesList.removeAt(i)
        currenciesData.value = currenciesList
    }

    override fun onCleared() {
        disposable?.dispose()
        super.onCleared()
    }
}