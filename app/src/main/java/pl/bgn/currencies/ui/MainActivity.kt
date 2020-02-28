package pl.bgn.currencies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import pl.bgn.currencies.R
import pl.bgn.currencies.adapters.SimpleRecyclerViewAdapter
import pl.bgn.currencies.databinding.ActivityMainBinding
import pl.bgn.currencies.viewmodels.CurrenciesViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrenciesViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: SimpleRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        linearLayoutManager = LinearLayoutManager(this)
        adapter = SimpleRecyclerViewAdapter(
            object : SimpleRecyclerViewAdapter.OnItemClickListener {
                override fun getCurrentFirstViewHolder(position: Int): SimpleRecyclerViewAdapter.SimpleViewHolder {
                    val id = viewModel.getCurrencyUniqueId(0)
                    viewModel.onResponderChange(position)
                    return binding.recyclerView.findViewHolderForItemId(id) as SimpleRecyclerViewAdapter.SimpleViewHolder
                }
            }
        )
        adapter.setHasStableIds(true)

        bind(binding)

        viewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        viewModel.currenciesData.observe(this, Observer { currencies ->
            currencies?.let { adapter.setCurrencies(it) } })
        viewModel.responder.observe(this, Observer { responder ->
            responder?.let { adapter.setResponder(it)} })
    }

    private fun bind(binding : ActivityMainBinding){
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}
