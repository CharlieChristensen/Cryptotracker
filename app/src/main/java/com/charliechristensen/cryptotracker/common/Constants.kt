package com.charliechristensen.cryptotracker.common

import java.util.Currency


object Constants {
    const val DefaultCurrency = "USD"

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
