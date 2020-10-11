package com.charliechristensen.remote.webservice

import com.charliechristensen.remote.models.RemoteCoinList
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.RemoteHistoryResponse
import com.charliechristensen.remote.models.RemoteTopListCoinData

/**
 * Api service
 */
interface CryptoService {

    suspend fun getCoinList(): RemoteCoinList

    suspend fun getFullCoinPrice(
        fromSymbols: String,
        toSymbols: String
    ): RemoteCoinPriceData

    suspend fun getHistoricalDataByMinute(
        fromSymbol: String,
        toSymbols: String,
        limit: Int = 1440,
        aggregate: Int = 1,
        exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    suspend fun getHistoricalDataByHour(
        fromSymbol: String,
        toSymbols: String,
        limit: Int = 168,
        aggregate: Int = 1,
        exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    suspend fun getHistoricalDataByDay(
        fromSymbol: String,
        toSymbols: String,
        limit: Int = 30,
        aggregate: Int = 1,
        exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    suspend fun getTopCoinFullData(
        toSymbols: String
    ): RemoteTopListCoinData
}

