package pl.bgn.currencies.data

import pl.bgn.currencies.getUniqueIdFromCurrencyName

class Model {
    data class Base(val baseCurrency: String, val rates: Map<String, Double>)
    data class Currency(val name: String, var rate: Double, val id: Long = getUniqueIdFromCurrencyName(name))
}