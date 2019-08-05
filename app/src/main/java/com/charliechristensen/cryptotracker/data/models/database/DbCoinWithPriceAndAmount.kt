package com.charliechristensen.cryptotracker.data.models.database

/**
 * Created by Chuck on 1/16/2018.
 */
class DbCoinWithPriceAndAmount(
    val symbol: String,
    val imageUrl: String,
    val price: Double = 0.0,
    val open24Hour: Double = 0.0,
    val amountOwned: Double = 0.0
)