package pl.bgn.currencies.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_simple.view.*
import pl.bgn.currencies.R
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemSimpleBinding
import pl.bgn.currencies.getUniqueIdFromCurrencyName
import java.math.RoundingMode
import java.text.DecimalFormat

class SimpleRecyclerViewAdapter(val clickListener: OnItemClickListener)
    : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleViewHolder>() {

    private var currencies = emptyList<Model.Currency>()
    private lateinit var responder: Model.Currency

    fun setCurrencies(currencies: List<Model.Currency>) {
        this.currencies = currencies
        notifyItemRangeChanged(1, currencies.size - 1)
    }

    fun setResponder(responder: Model.Currency) {
        this.responder = responder
    }

    inner class SimpleViewHolder(private val binding: RecyclerviewItemSimpleBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("onTextChanged()")
                if(s!!.isEmpty()) responder.rate = 0.0
                else responder.rate = s.toString().toDouble()
            }
        }

        fun bind(name: String, rate: Double) {
            if(name == responder.name) {
                binding.currencyName.text = name
                binding.editText.apply {
                    setText((computeCurrency(responder.rate)))
                    setOnClickListener(this@SimpleViewHolder)
                    addTextChangedListener(textWatcher)
                    println("TextWatcher set again")
                }
            } else {
                binding.currencyName.text = name
                binding.editText.apply {
                    setText(computeCurrency(rate))
                    setOnClickListener(this@SimpleViewHolder)
                }
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            moveOnClick(
                adapterPosition, itemView.editText,
                clickListener.getCurrentFirstViewHolder(adapterPosition).binding.editText)
        }

        fun removeTextWatcher() {
            if(binding.currencyName.text == responder.name) {
                println("Removing textWatcher...")
                binding.editText.removeTextChangedListener(textWatcher)
            }
        }
    }

    private fun moveOnClick(from: Int, clickedEditText: EditText, previousEditText: EditText){
        if(from != 0) {
            notifyItemMoved(from, 0)
            setEditTextFlag(previousEditText, false)
            setEditTextFlag(clickedEditText, true)
        } else setEditTextFlag(clickedEditText, true)
    }

    private fun setEditTextFlag(editText: EditText, flag: Boolean) {
        editText.isFocusableInTouchMode = flag
        if(flag) {
//            editText.addTextChangedListener(textWatcher)
            editText.requestFocus()
            val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        else {
//            editText.removeTextChangedListener(textWatcher)
            editText.clearFocus()
        }
    }


    private fun computeCurrency(rate: Double): String{
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        if(responder.rate == -1.0) return ""
        if(rate == responder.rate) {
            return df.format(rate)
        }
        val computed: Double = responder.rate * rate
        return df.format(computed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val binding: RecyclerviewItemSimpleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item_simple, parent, false
        )
        return SimpleViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return getUniqueIdFromCurrencyName(currencies[position].name)
    }

    override fun getItemCount() = currencies.size

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(currencies[position].name, currencies[position].rate)
    }

    override fun onViewRecycled(holder: SimpleViewHolder) {
        super.onViewRecycled(holder)
        holder.removeTextWatcher()
    }

    interface OnItemClickListener {
        fun getCurrentFirstViewHolder(position: Int): SimpleViewHolder
    }
}