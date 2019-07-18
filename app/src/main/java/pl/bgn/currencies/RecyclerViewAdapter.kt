package pl.bgn.currencies

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.bgn.currencies.databinding.RecyclerviewItemBinding

class RecyclerViewAdapter(private val currencies: ArrayList<Currency>) :
    RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.CurrencyHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: RecyclerViewAdapter.CurrencyHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Currency) {
            with(binding){

            }
        }
        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}