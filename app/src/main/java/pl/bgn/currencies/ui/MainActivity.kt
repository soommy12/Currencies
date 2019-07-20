package pl.bgn.currencies.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pl.bgn.currencies.R
import pl.bgn.currencies.adapters.RecyclerViewAdapter
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.ActivityMainBinding
import pl.bgn.currencies.network.ApiService
import pl.bgn.currencies.viewmodels.CurrenciesViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrenciesViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        binding.lifecycleOwner = this
        linearLayoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewAdapter()
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        viewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        viewModel.currenciesData.observe(this, Observer { currencies ->
            currencies?.let {
             adapter.setCurrencies(it) }
        })
    }
}
