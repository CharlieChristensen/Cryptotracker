package com.charliechristensen.remote.webservice

import com.charliechristensen.remote.models.RemoteCoinList
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.RemoteHistoryResponse
import com.charliechristensen.remote.models.RemoteTopListCoinData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service
 */
interface CryptoService {

    @GET("/data/all/coinlist")
    suspend fun getCoinList(): RemoteCoinList

    @GET("/data/pricemultifull")
    suspend fun getFullCoinPrice(
        @Query("fsyms") fromSymbols: String,
        @Query("tsyms") toSymbols: String
    ): RemoteCoinPriceData

    @GET("/data/histominute")
    suspend fun getHistoricalDataByMinute(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 1440,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    @GET("/data/histohour")
    suspend fun getHistoricalDataByHour(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 168,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    @GET("/data/histoday")
    suspend fun getHistoricalDataByDay(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 30,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): RemoteHistoryResponse

    @GET("/data/top/totalvolfull")
    suspend fun getTopCoinFullData(
        @Query("tsym") toSymbols: String
    ): RemoteTopListCoinData
}
