package com.charliechristensen.remote.webservice

import com.charliechristensen.remote.models.RemoteCoinList
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.RemoteHistoryResponse
import com.charliechristensen.remote.models.RemoteTopListCoinData
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class KtorCryptoService constructor(
    private val webSocketUrl: String,
    private val client: HttpClient,
    private val webSocketClient: HttpClient
) : CryptoService {

    override suspend fun getCoinList(): RemoteCoinList = client.get("/data/all/coinlist")

    override suspend fun getFullCoinPrice(
        fromSymbols: String,
        toSymbols: String
    ): RemoteCoinPriceData {
        listenToWebSocket()
        return client.get("/data/pricemultifull") {
            parameter("fsyms", fromSymbols)
            parameter("tsyms", toSymbols)
        }
    }

    override suspend fun getHistoricalDataByMinute(
        fromSymbol: String,
        toSymbols: String,
        limit: Int,
        aggregate: Int,
        exchange: String
    ): RemoteHistoryResponse = client.get("/data/histominute") {
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
        parameter("fsym", fromSymbol)
        parameter("tsym", toSymbols)
        parameter("limit", limit)
        parameter("aggregate", aggregate)
        parameter("e", exchange)
    }

    override suspend fun getTopCoinFullData(
        toSymbols: String
    ): RemoteTopListCoinData = client.get("/data/top/totalvolfull") {
        parameter("tsym", toSymbols)
    }

    suspend fun listenToWebSocket() {
        webSocketClient.webSocket {
            Timber.d("Evenk it to here:")
            incoming.consumeAsFlow()
                .onEach { frame ->
                    when (frame) {
                        is Frame.Binary -> {
                            Timber.d("Evenk: ${frame.readBytes()}")
                        }
                        is Frame.Text -> {
                            Timber.d("Evenk: ${frame.readText()}")
                        }
                        is Frame.Close -> {
                            Timber.d("Evenk: Close")
                        }
                        is Frame.Ping -> {
                            Timber.d("Evenk: Ping")
                        }
                        is Frame.Pong -> {
                            Timber.d("Evenk: Pong")
                        }
                    }
                }
                .collect()
        }
    }

}
