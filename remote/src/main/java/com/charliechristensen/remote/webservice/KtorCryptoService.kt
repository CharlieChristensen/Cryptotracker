package com.charliechristensen.remote.webservice

import com.charliechristensen.remote.models.RemoteCoinList
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.RemoteHistoryResponse
import com.charliechristensen.remote.models.RemoteTopListCoinData
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol

class KtorCryptoService constructor(
    private val baseUrl: String,
    private val client: HttpClient
) : CryptoService {

    override suspend fun getCoinList(): RemoteCoinList = client.get("/data/all/coinlist") {
        setDefaultParams()
    }

    override suspend fun getFullCoinPrice(
        fromSymbols: String,
        toSymbols: String
    ): RemoteCoinPriceData = client.get("/data/pricemultifull") {
        setDefaultParams()
        parameter("fsyms", fromSymbols)
        parameter("tsyms", toSymbols)
    }

    override suspend fun getHistoricalDataByMinute(
        fromSymbol: String,
        toSymbols: String,
        limit: Int,
        aggregate: Int,
        exchange: String
    ): RemoteHistoryResponse = client.get("/data/histominute") {
        setDefaultParams()
        parameter("fsym", fromSymbol)
        parameter("tsym", toSymbols)
        parameter("limit", limit)
        parameter("aggregate", aggregate)
        parameter("e", exchange)
    }

    override suspend fun getHistoricalDataByHour(
        fromSymbol: String,
        toSymbols: String,
        limit: Int,
        aggregate: Int,
        exchange: String
    ): RemoteHistoryResponse = client.get("/data/histohour") {
        setDefaultParams()
        parameter("fsym", fromSymbol)
        parameter("tsym", toSymbols)
        parameter("limit", limit)
        parameter("aggregate", aggregate)
        parameter("e", exchange)
    }

    override suspend fun getHistoricalDataByDay(
        fromSymbol: String,
        toSymbols: String,
        limit: Int,
        aggregate: Int,
        exchange: String
    ): RemoteHistoryResponse = client.get("/data/histoday") {
        setDefaultParams()
        parameter("fsym", fromSymbol)
        parameter("tsym", toSymbols)
        parameter("limit", limit)
        parameter("aggregate", aggregate)
        parameter("e", exchange)
    }

    override suspend fun getTopCoinFullData(
        toSymbols: String
    ): RemoteTopListCoinData = client.get("/data/top/totalvolfull") {
        setDefaultParams()
        parameter("tsym", toSymbols)
    }

    private fun HttpRequestBuilder.setDefaultParams() {
        url.protocol = URLProtocol.HTTPS
        url.host = baseUrl
    }
}
