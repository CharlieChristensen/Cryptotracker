package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.database.models.DbCoin
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.database.models.DbCoinWithPriceAndAmount

fun DbCoin.toUi(): Coin = Coin(
    imageUrl,
    symbol,
    coinName
)

fun DbCoinPriceData.toUi() = CoinPriceData(
    symbol,
    price,
    open24Hour,
    high24Hour,
    low24Hour
)

fun DbCoinWithPriceAndAmount.toUi() = CoinWithPriceAndAmount(
    symbol,
    imageUrl,
    price,
    open24Hour,
    amountOwned
)
