package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.database.models.DbCoin
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.remote.models.ServerCoinData
import com.charliechristensen.remote.models.ServerCoinPriceRawData

/**
 * Created by Chuck on 1/20/2018.
 */
object NetworkToDbMapper {

    fun mapCoin(coinData: ServerCoinData, baseImageUrl: String): DbCoin {
        return DbCoin(
            coinData.symbol,
            baseImageUrl + coinData.imageUrl,
            coinData.coinName,
            coinData.sortOrder
        )
    }

    fun mapCoinPriceData(networkCoin: ServerCoinPriceRawData): DbCoinPriceData {
        return DbCoinPriceData(
            networkCoin.fromSymbol,
            networkCoin.price,
            networkCoin.open24Hour,
            networkCoin.high24Hour,
            networkCoin.low24Hour
        )
    }
}
