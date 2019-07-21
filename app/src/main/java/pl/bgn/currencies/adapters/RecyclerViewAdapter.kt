package pl.bgn.currencies.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.tools.build.jetifier.core.utils.Log
import pl.bgn.currencies.R
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class RecyclerViewAdapter(val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerViewAdapter.CurrencyHolder>() {

    private final val TAG = "RecyclerAdapter"
    private var currencies = emptyList<Model.Currency>()
    private var responder: Model.Responder = Model.Responder("EUR", 10.0)

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

        fun bindBase(){
            Log.e(TAG, "bindBase")
            with(binding){
                Log.e(TAG, "currency: ${responder.name}")
                currencyName.text = responder.name
                currencyValue.setText(responder.amount.toString())
                Log.e(TAG, "TAKI MAMY ${currencyValue.text}")
                currencyValue.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        Log.e(TAG, "beforeTextChanged: $s")
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        Log.e(TAG, "GURWA LICZBA TAKA: $s")
//                        if(s.toString() == "") responder.amount = -1.0
//                        else responder.amount = s.toString().toDouble()

                    }

                    override fun afterTextChanged(s: Editable?) {
                        Log.e(TAG, "afterTextChanged: $s")
                        Log.e(TAG, "TAKI MAMY ${currencyValue.text}")
//                        currencyValue.text = s
//                        Log.e("TAG", "Gurwa")
//                        var str  = s.toString()
//                        str = str.replace(".", ",")
//                        currencyValue.setText(str)
//                        notifyItemRangeChanged(1, currencies.size)
                    }

                })

            }
        }

        fun bind(currency: Model.Currency) {
            with(binding){
                currencyName.text = currency.name
                currencyValue.setText(computeCurrency(currency.rate))
//                itemView.setOnClickListener(this@CurrencyHolder)
            }
        }

        override fun onClick(v: View?) {
            Log.e("onClick()", "registered")
            val currencyClicked = currencies[adapterPosition]
            Log.e("onClick()", currencyClicked.name)
            responder = Model.Responder(currencyClicked.name, currencyClicked.rate)
            notifyItemMoved(adapterPosition, 0)
            itemClickListener.onCurrencyResponderChange(currencyClicked.name)

        }

    }

    fun computeCurrency(rate: Double): String{
        if(responder.amount == -1.0) return ""
        val computed: Double = responder.amount * rate
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(computed)
    }

    interface OnItemClickListener {
        fun onCurrencyResponderChange(responderName: String)
    }

}