package pl.bgn.currencies.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.bgn.currencies.R
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class RecyclerViewAdapter(val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    private val TAG = "RecyclerAdapter"
    private var currencies = emptyList<Model.Currency>()
    private lateinit var responder: Model.Currency

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val binding: RecyclerviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item, parent, false)
        return CurrencyHolder(binding)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        holder.bind(currencies[position])
    }

    fun setResponder(responder: Model.Currency) {
        println("NEW_RESPONDER ${responder.name}")
        this.responder = responder
//        notifyDataSetChanged()
    }

    fun setCurrencies(currencies: List<Model.Currency>) {
        this.currencies = currencies
        notifyDataSetChanged()
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Model.Currency) {

            if(currency.name == responder.name) itemView.setOnClickListener(null)
            else itemView.setOnClickListener(this)
            with(binding){
                currencyName.text = currency.name
                currencyValue.text = computeCurrency(currency.rate)
            }
        }

        override fun onClick(v: View?) {
            v?.isClickable = false
            itemClickListener.onCurrencyResponderChange(adapterPosition)
            notifyItemMoved(adapterPosition, 0)
        }
    }

    fun computeCurrency(rate: Double): String{
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        if(responder.rate == -1.0) return ""
        if(rate == responder.rate) {
            println("To responder: ${responder.rate}")
            return df.format(rate)
        }
        val computed: Double = responder.rate * rate
        return df.format(computed)
    }

    interface OnItemClickListener {
        fun onCurrencyResponderChange(position: Int)
    }
}