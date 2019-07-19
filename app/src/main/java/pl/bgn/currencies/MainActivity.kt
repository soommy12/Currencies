package pl.bgn.currencies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import pl.bgn.currencies.databinding.ActivityMainBinding
import pl.bgn.currencies.network.ApiService
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerViewAdapter
    private val apiService by lazy { ApiService.create() }
    var disposable: Disposable? = null

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
                    Toast.makeText(this, "NO ELO", Toast.LENGTH_SHORT).show()
                    Log.e("Currencies", "result: ${result.base} ${result.date} ${result.rates}")
                },
                { error ->
                    Log.e("Currencies", "Problem: ${error.message}")
                    Toast.makeText(this, "ERR", Toast.LENGTH_SHORT).show() }
                )
        adapter = RecyclerViewAdapter(ArrayList())
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
