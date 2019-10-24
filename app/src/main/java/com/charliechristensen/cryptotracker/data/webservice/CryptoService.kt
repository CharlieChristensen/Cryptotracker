package com.charliechristensen.cryptotracker.data.webservice

import com.charliechristensen.cryptotracker.data.models.network.ServerCoinList
import com.charliechristensen.cryptotracker.data.models.network.ServerCoinPriceData
import com.charliechristensen.cryptotracker.data.models.network.ServerHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service
 */
interface CryptoService {

    @GET("/data/all/coinlist")
    suspend fun getCoinList(): ServerCoinList

    @GET("/data/pricemultifull")
    suspend fun getFullCoinPrice(
        @Query("fsyms") fromSymbols: String,
        @Query("tsyms") toSymbols: String
    ): ServerCoinPriceData

    @GET("/data/histominute")
    suspend fun getHistoricalDataByMinute(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 1440,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): ServerHistoryResponse

    @GET("/data/histohour")
    suspend fun getHistoricalDataByHour(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 168,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): ServerHistoryResponse

    @GET("/data/histoday")
    suspend fun getHistoricalDataByDay(
        @Query("fsym") fromSymbol: String,
        @Query("tsym") toSymbols: String,
        @Query("limit") limit: Int = 30,
        @Query("aggregate") aggregate: Int = 1,
        @Query("e") exchange: String = "CCCAGG"
    ): ServerHistoryResponse

}