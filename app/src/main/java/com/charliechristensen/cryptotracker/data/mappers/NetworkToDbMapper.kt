package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.database.DbCoinPriceData
import com.charliechristensen.network.models.ServerCoinData
import com.charliechristensen.network.models.ServerCoinPriceRawData

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