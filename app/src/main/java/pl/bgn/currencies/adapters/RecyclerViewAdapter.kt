package pl.bgn.currencies.adapters

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.tools.build.jetifier.core.utils.Log
import pl.bgn.currencies.R
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemBaseBinding
import pl.bgn.currencies.databinding.RecyclerviewItemBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class RecyclerViewAdapter(val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "RecyclerAdapter"
    private var currencies = emptyList<Model.Currency>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                val binding: RecyclerviewItemBaseBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.recyclerview_item_base, parent, false)
                BaseHolder(binding)
            }
            else -> {
                val binding: RecyclerviewItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.recyclerview_item, parent, false)
                CurrencyHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> 0
            else -> 1
        }
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0 -> {
                val viewHolder = holder as BaseHolder
                viewHolder.bind()
            }
            else -> {
                val viewHolder = holder as CurrencyHolder
                viewHolder.itemView.setOnClickListener {
                    val currencyClicked = currencies[position]
                    notifyItemMoved(position, 0)
                    itemClickListener.onCurrencyResponderChange(currencyClicked.name)
                }
                viewHolder.bind(currencies[position])
            }
        }
    }

    fun setCurrencies(currencies: List<Model.Currency>) {
        this.currencies = currencies
        notifyItemRangeChanged(1, currencies.size)
    }

    inner class BaseHolder(private val binding: RecyclerviewItemBaseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(){
            with(binding){
                baseName.text = currencies[0].name
//                baseValue.setText(currencies[0].rate.toString())

//                baseValue.addTextChangedListener(object : TextWatcher {
//
//                    var isEditing: Boolean = false
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    }
//
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    }
//
//                    override fun afterTextChanged(s: Editable?) {
//                        if(isEditing) return
//                        else {
//                            isEditing = true
//                            if(s.toString() == "") currencies[0].rate = 0.0
//                            else currencies[0].rate = s.toString().toDouble()
//                            baseValue.text = s
//                            isEditing = false
//                        }
//                    }
//
//                })
            }
        }
    }

    inner class CurrencyHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(currency: Model.Currency) {
            itemView.setOnClickListener(this)
            with(binding){
                currencyName.text = currency.name
                currencyValue.setText(computeCurrency(currency.rate))
//                currencyName.isFocusableInTouchMode = false
//                currencyValue.isFocusableInTouchMode = false
            }
        }

        override fun onClick(v: View?) {
            val currencyClicked = currencies[adapterPosition]
            itemClickListener.onCurrencyResponderChange(currencyClicked.name)
        }
    }

    fun computeCurrency(rate: Double): String{
        if(currencies[0].rate == -1.0) return ""
        val computed: Double = currencies[0].rate * rate
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(computed)
    }

    interface OnItemClickListener {
        fun onCurrencyResponderChange(responderName: String)
    }
}