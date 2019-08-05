package com.charliechristensen.network

import com.charliechristensen.network.models.ServerCoinList
import com.charliechristensen.network.models.ServerCoinPriceData
import com.charliechristensen.network.models.ServerHistoryResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service
 */
interface CryptoService {

    @GET("/data/all/coinlist")
    fun getCoinList() : Single<ServerCoinList>

    @GET("/data/pricemultifull")
    fun getFullCoinPrice(@Query("fsyms") fromSymbols: String,
                         @Query("tsyms") toSymbols: String): Single<ServerCoinPriceData>

    @GET("/data/histominute")
    fun getHistoricalDataByMinute(@Query("fsym") fromSymbol: String,
                                  @Query("tsym") toSymbols: String,
                                  @Query("limit") limit: Int = 1440,
                                  @Query("aggregate") aggregate: Int = 1,
                                  @Query("e") exchange: String = "CCCAGG") : Single<ServerHistoryResponse>

    @GET("/data/histohour")
    fun getHistoricalDataByHour(@Query("fsym") fromSymbol: String,
                                @Query("tsym") toSymbols: String,
                                @Query("limit") limit: Int = 168,
                                @Query("aggregate") aggregate: Int = 1,
                                @Query("e") exchange: String = "CCCAGG") : Single<ServerHistoryResponse>

    @GET("/data/histoday")
    fun getHistoricalDataByDay(@Query("fsym") fromSymbol: String,
                               @Query("tsym") toSymbols: String,
                               @Query("limit") limit: Int = 30,
                               @Query("aggregate") aggregate: Int = 1,
                               @Query("e") exchange: String = "CCCAGG") : Single<ServerHistoryResponse>

}