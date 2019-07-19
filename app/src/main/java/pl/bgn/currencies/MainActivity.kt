package pl.bgn.currencies

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.ActivityMainBinding
import pl.bgn.currencies.network.ApiService

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    private val apiService by lazy { ApiService.create() }
    var disposable: Disposable? = null
    val currenciesList: ArrayList<Model.Currency> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        linearLayoutManager = LinearLayoutManager(this)
        disposable = apiService.getLatestCurrencyRate("EUR")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    updateCurrencies(result.rates)
                    adapter = RecyclerViewAdapter(currenciesList)
                    binding.recyclerView.layoutManager = linearLayoutManager
                    binding.recyclerView.adapter = adapter
                    Toast.makeText(this, "NO ELO", Toast.LENGTH_SHORT).show()
                    Log.e("Currencies", "result: ${result.base} ${result.date} ${result.rates}")
                },
                { error ->
                    Log.e("Currencies", "Problem: ${error.message}")
                    Toast.makeText(this, "ERR", Toast.LENGTH_SHORT).show() }
                )

    }

    private fun updateCurrencies(rates: Model.Rates) {
        currenciesList.add(Model.Currency("AUD", rates.AUD))
        currenciesList.add(Model.Currency("BGN", rates.BGN))
        currenciesList.add(Model.Currency("BRL", rates.BRL))
        currenciesList.add(Model.Currency("CAD", rates.CAD))
        currenciesList.add(Model.Currency("CHF", rates.CHF))
        currenciesList.add(Model.Currency("CNY", rates.CNY))
        currenciesList.add(Model.Currency("CZK", rates.CZK))
        currenciesList.add(Model.Currency("DKK", rates.DKK))
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
        currenciesList.add(Model.Currency("EUR", rates.EUR))
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
