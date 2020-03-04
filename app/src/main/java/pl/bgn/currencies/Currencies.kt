package pl.bgn.currencies

import android.widget.EditText

fun getUniqueIdFromCurrencyName(name: String): Long {
    val sb = StringBuilder()
    for(ch in  name.toCharArray())
        sb.append(ch.toByte())
    return sb.toString().toLong()
}

fun EditText.setCursorAtEnd() {
    this.setSelection(this.text.length)
}