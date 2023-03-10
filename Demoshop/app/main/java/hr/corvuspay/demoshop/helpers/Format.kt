package hr.corvuspay.demoshop.helpers

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object Format {

    private val dec = DecimalFormat("#,###.00").apply {
        decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }
    }

    fun formatPrice(amount: Double, currency: String): String {
        return "${dec.format(amount)} $currency"
    }
}