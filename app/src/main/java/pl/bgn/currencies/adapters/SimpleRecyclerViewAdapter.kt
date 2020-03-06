package pl.bgn.currencies.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.bgn.currencies.R
import pl.bgn.currencies.data.Model
import pl.bgn.currencies.databinding.RecyclerviewItemSimpleBinding
import pl.bgn.currencies.setCursorAtEnd
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern


class SimpleRecyclerViewAdapter(val clickListener: OnItemClickListener)
    : RecyclerView.Adapter<SimpleRecyclerViewAdapter.SimpleViewHolder>() {

    private var currencies = emptyList<Model.Currency>()
    private lateinit var responder: Model.Currency
    var isConnected = true


    fun setCurrencies(currencies: List<Model.Currency>) {
        this.currencies = currencies
        notifyItemRangeChanged(1, currencies.size - 1)
    }

    fun setResponder(responder: Model.Currency) {
        this.responder = responder
        notifyItemRangeChanged(1, currencies.size - 1)
    }

    inner class SimpleViewHolder(
        val binding: RecyclerviewItemSimpleBinding
    )
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var shouldIgnore = false

        val textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if(shouldIgnore) return
                s?.let {
                    shouldIgnore = true
                    if(s.toString().isEmpty()) {
                        responder.rate = 0.0
                        binding.editText.setText("0")
                    }
                    else {
                        binding.editText.setText(setupEditTextValue(s))
                        responder.rate = s.toString().replace(",", ".").toDouble()
                    }
                    notifyItemRangeChanged(1, currencies.size - 1)
                    binding.editText.setCursorAtEnd()
                    shouldIgnore = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        inner class CurrencyInputFilter: InputFilter {

            private val pattern: Pattern = Pattern.compile("[0-9]{0,8}((\\.[0-9]?)?)|(\\.)?")

            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val matcher = pattern.matcher(dest)
                return if (!matcher.matches()) "" else null
            }
        }

        private fun setupEditTextValue(s: CharSequence): String {
            val str = s.toString()
            return if(str.startsWith("0") && str.length > 1 && str[1].toString() != ".")
                str.substring(1)
            else str
        }

        fun bind(name: String, rate: Double) {
            val computedCurrency: String
            setFlagAndLongName(name)
            binding.currencyName.text = name
            if(name == responder.name) {
                computedCurrency = computeCurrency(responder.rate)
                binding.editText.apply {
                    setText(computedCurrency)
                    setTextColor(ContextCompat.getColor(context, R.color.semiBlack))
                    addTextChangedListener(textWatcher)
                }
            } else {
                computedCurrency = computeCurrency(rate)
                binding.editText.apply{
                    setText(computedCurrency)
                    if(binding.editText.text.toString() == "0") setTextColor(ContextCompat.getColor(context, R.color.semiGray))
                    else setTextColor(ContextCompat.getColor(context, R.color.semiBlack))
                }
            }
            binding.editText.apply {
                filters = arrayOf(CurrencyInputFilter())
                keyListener = DigitsKeyListener(true, true)
                setOnClickListener(this@SimpleViewHolder)
            }
            itemView.setOnClickListener(this)
        }

        private fun setFlagAndLongName(name: String) {
            when(name) {
                "USD" -> {
                    binding.flagView.apply { setImageDrawable(ContextCompat.getDrawable(context, R.drawable.us)) }
                    binding.currencyLongName.text = "US Dollar"
                }
                "SEK" -> {
                    binding.flagView.apply { setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sweden)) }
                    binding.currencyLongName.text = "Swedish Korona"
                }
                "CAD" -> {
                    binding.flagView.apply { setImageDrawable(ContextCompat.getDrawable(context, R.drawable.canada)) }
                    binding.currencyLongName.text = "Canadian Dollar"
                }
                "EUR" -> {
                    binding.flagView.apply { setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eu)) }
                    binding.currencyLongName.text = "Euro"
                }
                else -> {
                    binding.flagView.apply { setImageDrawable(ContextCompat.getDrawable(context, R.drawable.emoji)) }
                    binding.currencyLongName.text = "Full Name"
                }
            }

        }

        override fun onClick(v: View?) {
            if(v !is EditText) binding.editText.setCursorAtEnd()
            when {
                isConnected -> moveOnClick(
                    adapterPosition, this,
                    clickListener.getCurrentFirstViewHolder(adapterPosition, binding.editText.text.toString()))
                adapterPosition != 0 -> {
                    Toast.makeText(binding.root.context, R.string.no_connection, Toast.LENGTH_SHORT).show()
                    hideKeyboard(binding.editText)
                }
                else -> showKeyboard(binding.editText)
            }
        }

        fun removeTextWatcher() {
            if(binding.currencyName.text == responder.name) {
                binding.editText.apply{
                    removeTextChangedListener(textWatcher)
                }
            }
        }
    }

    private fun moveOnClick(from: Int, clickedHolder: SimpleViewHolder, previousHolder: SimpleViewHolder?){
        if(from != 0) {
            notifyItemMoved(from, 0)
            notifyItemRangeChanged(1, currencies.size - 1)
            configureHolder(previousHolder, false)
            configureHolder(clickedHolder, true)
        } else showKeyboard(clickedHolder.binding.editText)
    }

    private fun configureHolder(holder: SimpleViewHolder?, flag: Boolean) {
        holder?.let {
            val editText = holder.binding.editText
            editText.isFocusableInTouchMode = flag
            if(flag) {
                editText.requestFocus()
                val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
            else {
                editText.removeTextChangedListener(holder.textWatcher)
                editText.clearFocus()
            }
        }
    }

    private fun showKeyboard(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun computeCurrency(rate: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        if(responder.rate == -1.0) return ""
        if(rate == responder.rate) {
            return df.format(rate).replace(",", ".")
        }
        val computed: Double = responder.rate * rate
        return df.format(computed).replace(",", ".")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val binding: RecyclerviewItemSimpleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_item_simple, parent, false
        )
        return SimpleViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return currencies[position].id
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
        fun getCurrentFirstViewHolder(position: Int, value: String): SimpleViewHolder?
    }
}