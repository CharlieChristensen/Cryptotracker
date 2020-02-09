package com.charliechristensen.cryptotracker.common

import java.util.Currency

/**
 * Created by Chuck on 1/2/2018.
 */
object Constants {
    const val DefaultCurrency = "USD"
    const val Euro = "EUR"

    val availableCurrencies =
        arrayOf(DefaultCurrency)
            .plus(
                Currency.getAvailableCurrencies()
                    .mapNotNull { it.currencyCode }
                    .sorted()
            )
            .distinct()
            .toTypedArray()

    val availableThemes = listOf(
        AppTheme.Dark,
        AppTheme.Teal
    )

}
