package com.charliechristensen.cryptotracker.data.models.ui

data class CoinPriceData(
    val symbol: String,
    val price: Double,
    val open24Hour: Double,
    val high24Hour: Double,
    val low24Hour: Double
)
