package com.charliechristensen.database.models


class DbCoinWithPriceAndAmount(
    val symbol: String,
    val imageUrl: String,
    val price: Double = 0.0,
    val open24Hour: Double = 0.0,
    val amountOwned: Double = 0.0
)
