package com.charliechristensen.cryptotracker.common

import java.text.NumberFormat
import java.util.Currency

class FormatterFactory {

    fun currencyFormatter(currencyCode: String): NumberFormat =
            NumberFormat.getCurrencyInstance().apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 6
                currency = Currency.getInstance(currencyCode)
            }

    fun decimalFormatter(): NumberFormat =
            NumberFormat.getInstance().apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 6
            }

    fun percentFormatter(): NumberFormat =
            NumberFormat.getPercentInstance().apply {
                minimumFractionDigits = 2
            }
}
