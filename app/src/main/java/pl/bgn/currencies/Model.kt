package pl.bgn.currencies

import java.util.*

class Model {
    data class Rates(
        val AUD: Double)
    data class Base(val base: String, val date: Date, val rates: Rates)
}
