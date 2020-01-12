package com.charliechristensen.cryptotracker.data.models.ui

data class CoinWithPriceAndAmount(
    val symbol: String,
    val imageUrl: String,
    val price: Double,
    val open24Hour: Double,
    val amountOwned: Double
)
