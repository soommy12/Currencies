package pl.bgn.currencies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.bgn.currencies.databinding.RecyclerviewItemBinding

class RecyclerViewAdapter(private val currencies: ArrayList<Model.Rates>) :
    RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.CurrencyHolder {
        val binding: RecyclerviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item, parent, false)
        return CurrencyHolder(binding)
    }

    override fun getItemCount() = /*currencies.size*/ 10

    override fun onBindViewHolder(holder: RecyclerViewAdapter.CurrencyHolder, position: Int) {
//        holder.bind(currencies[position])
        holder.bind(Model.Rates(2.23))
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Model.Rates) {
            with(binding){
                countryName.text = "Poland"
                currencyName.text = "PLN"
                currencyValue.setText("10")
            }
        }
        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}