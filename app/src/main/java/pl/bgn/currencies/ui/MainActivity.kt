package pl.bgn.currencies.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_main.*
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
        supportActionBar?.hide()
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        linearLayoutManager = LinearLayoutManager(this)
        adapter = SimpleRecyclerViewAdapter(
            object : SimpleRecyclerViewAdapter.OnItemClickListener {
                override fun getCurrentFirstViewHolder(position: Int, value: String): SimpleRecyclerViewAdapter.SimpleViewHolder? {
                    val id = viewModel.getCurrencyUniqueId(0)
                    viewModel.onResponderChange(position, value)
                    val holder = binding.recyclerView.findViewHolderForItemId(id)
                    return if(holder == null) null
                    else holder as SimpleRecyclerViewAdapter.SimpleViewHolder
                }
            }
        )
        adapter.setHasStableIds(true)

        bind(binding)

        viewModel = ViewModelProviders.of(this).get(CurrenciesViewModel::class.java)
        viewModel.connectionLiveData.observe(this, Observer {
            if(!viewModel.currenciesVisible && it) {
                viewModel.startInterval()
                with(binding) {
                    if(infoText.isVisible) {
                        infoText.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        showToast(R.string.reconnect)
                    }
                }
            }
            else if(!viewModel.currenciesVisible && !it) {
                with(binding) {
                    infoText.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
            else if(viewModel.currenciesVisible && it) {
                showToast(R.string.reconnect)
                adapter.isConnected = true
                viewModel.startInterval()
            }
            else if(viewModel.currenciesVisible && !it){
                showToast(R.string.lost_connection)
                viewModel.stopFetch()
                adapter.isConnected = false
            }
        })
        viewModel.progressBarVisible.observe(this, Observer {
            if(it) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        })
        viewModel.currenciesData.observe(this, Observer {
                currencies -> currencies?.let { adapter.setCurrencies(it)
            }
        })
        viewModel.responder.observe(this, Observer {
                responder -> responder?.let { adapter.setResponder(it)
            }
        })
    }

    private fun bind(binding : ActivityMainBinding){
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this@MainActivity, stringId, Toast.LENGTH_SHORT).show()
    }
}
