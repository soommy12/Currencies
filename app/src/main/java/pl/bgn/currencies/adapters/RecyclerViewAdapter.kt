package pl.bgn.currencies.adapters

import android.text.Editable
import android.text.TextWatcher
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

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    private var currencies = emptyList<Model.Currency>()
    private var responder = Model.Responder("EUR", 10.0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val binding: RecyclerviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item, parent, false)
        return CurrencyHolder(binding)
    }

    override fun getItemCount() = currencies.size + 1

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        if(position == 0) holder.bindBase()
        else holder.bind(currencies[position - 1])
    }

    fun setCurrencies(currencies: List<Model.Currency>) {
        this.currencies = currencies
        notifyItemRangeChanged(1, currencies.size)
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Model.Currency) {
            with(binding){
                currencyName.text = currency.name
                currencyValue.setText(computeCurrency(currency.rate))
            }
        }

        fun bindBase(){
            with(binding){
                currencyName.text = responder.name
                currencyValue.setText(responder.amount.toString())
//                currencyValue.addTextChangedListener(object : TextWatcher {
//                    override fun afterTextChanged(s: Editable?) {
//                        responder.amount = s.toString().toDouble()
//                        notifyItemRangeChanged(1, currencies.size)
//                    }
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
//
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//                })
            }
        }
        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    fun computeCurrency(rate: Double): String{
        val computed: Double = responder.amount * rate
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(computed)
    }

}