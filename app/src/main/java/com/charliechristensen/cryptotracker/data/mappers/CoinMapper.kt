package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
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
    val dbCoinPriceDataMapper: (String, String, Double, Double, Double, Double) -> CoinPriceData =
        { symbol, currency, price, open24Hour, high24Hour, low24Hour ->
            CoinPriceData(symbol, currency, price, open24Hour, high24Hour, low24Hour)
        }

    @JvmStatic
    val dbCoinHistoryMapper: (Long, Double, Double, Double, Double, Double, Double) -> CoinHistoryElement =
        { time, close, high, low, open, volumeFrom, volumeTo ->
            CoinHistoryElement(time, close, high, low, open, volumeFrom, volumeTo)
        }

}
