package pl.bgn.currencies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemBinding

class RecyclerViewAdapter(private val currencies: ArrayList<Model.Currency>) :
    RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.CurrencyHolder {
        val binding: RecyclerviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item, parent, false)
        return CurrencyHolder(binding)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: RecyclerViewAdapter.CurrencyHolder, position: Int) {
        holder.bind(currencies[position])
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Model.Currency) {
            with(binding){
                countryName.text = "Poland"
                currencyName.text = currency.name
                currencyValue.setText(currency.rate.toString())
            }
        }
        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}