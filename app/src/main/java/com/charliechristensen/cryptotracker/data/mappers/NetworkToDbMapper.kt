package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.database.models.DbCoin
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.remote.models.RemoteCoinData
import com.charliechristensen.remote.models.RemoteCoinPriceRawData

/**
 * Created by Chuck on 1/20/2018.
 */
object NetworkToDbMapper {

    fun mapCoin(coinData: RemoteCoinData, baseImageUrl: String): DbCoin {
        return DbCoin(
            coinData.symbol,
            baseImageUrl + coinData.imageUrl,
            coinData.coinName,
            coinData.sortOrder
        )
    }

    fun mapCoinPriceData(networkCoin: RemoteCoinPriceRawData): DbCoinPriceData {
        return DbCoinPriceData(
            networkCoin.fromSymbol,
            networkCoin.price,
            networkCoin.open24Hour,
            networkCoin.high24Hour,
            networkCoin.low24Hour
        )
    }
}
