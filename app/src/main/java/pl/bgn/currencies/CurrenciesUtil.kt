package pl.bgn.currencies

fun getUniqueIdFromCurrencyName(name: String): Long {
    val sb = StringBuilder()
    for(ch in  name.toCharArray())
        sb.append(ch.toByte())
    return sb.toString().toLong()
}