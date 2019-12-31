package com.charliechristensen.cryptotracker.common

import dagger.Reusable
import java.text.NumberFormat
import javax.inject.Inject

@Reusable
class FormatterFactory @Inject constructor() {

    fun currencyFormatter(): NumberFormat =
            NumberFormat.getCurrencyInstance().apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 6
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
