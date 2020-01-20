package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount

object CoinMappers {

    @JvmStatic
    val dbCoinMapper: (String, String?, String) -> Coin =
        { sym, imageUrl, coinName ->
            Coin(imageUrl, sym, coinName)
        }

    @JvmStatic
    val dbCoinWithPriceAndAmountMapper: (String, String?, Double, Double, Double) -> CoinWithPriceAndAmount =
        { symbol, imageUrl, price, open24Hour, amountOwned ->
            CoinWithPriceAndAmount(symbol, imageUrl ?: "", price, open24Hour, amountOwned)
        }

    @JvmStatic
    val dbCoinPriceDataMapper: (String, Double, Double, Double, Double) -> CoinPriceData =
        { symbol, price, open24Hour, high24Hour, low24Hour ->
            CoinPriceData(symbol, price, open24Hour, high24Hour, low24Hour)
        }

}
