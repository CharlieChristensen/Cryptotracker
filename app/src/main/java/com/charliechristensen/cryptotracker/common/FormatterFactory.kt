package com.charliechristensen.cryptotracker.common

import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Chuck on 1/16/2018.
 */
@Singleton
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