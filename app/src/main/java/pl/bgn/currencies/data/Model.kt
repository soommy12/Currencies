package pl.bgn.currencies.data

import java.util.*

class Model {
    data class Base(val base: String, val date: Date, val rates: Map<String, Double>)
    data class Currency(val name: String, var rate: Double)
}