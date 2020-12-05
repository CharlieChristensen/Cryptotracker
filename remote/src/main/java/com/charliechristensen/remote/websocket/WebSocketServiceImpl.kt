package com.charliechristensen.remote.websocket

import com.charliechristensen.remote.models.SymbolPricePair
import com.charliechristensen.remote.models.WebSocketCoinData
import com.charliechristensen.remote.models.WebSocketSubscription
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.wss
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.FrameType
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class WebSocketServiceImpl constructor(
    private val client: HttpClient,
    private val json: Json
) : WebSocketService {

    private var currency: String? = "USD" //TODO Inject this from prefs
    private val temporarySubscriptions: MutableSet<String> = mutableSetOf()
    private val portfolioSubscriptions: MutableSet<String> = mutableSetOf()

    private val priceUpdatesFlow = MutableStateFlow(SymbolPricePair("", "", 0.0))

    private var session: DefaultClientWebSocketSession? = null

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    init {
        connect()
    }

    override fun connect() {
        session?.cancel()
        scope.launch {
            client.wss(host = "streamer.cryptocompare.com/v2") {
                session = this
                val currency = currency
                if (!currency.isNullOrBlank()) {
                    addSubscriptions(
                        temporarySubscriptions.plus(portfolioSubscriptions).toList(),
                        currency
                    )
                }

                incoming.consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .map { frame -> frame.readText() }
                    .mapNotNull { frame -> json.decodeFromString<WebSocketCoinData>(frame) }
                    .filter { coinData -> coinData.type == 5 }
                    .mapNotNull { coinData ->
                        SymbolPricePair(
                            coinData.fromSymbol ?: return@mapNotNull null,
                            coinData.toSymbol ?: return@mapNotNull null,
                            coinData.price ?: return@mapNotNull null
                        )
                    }
                    .onEach { symbolPricePair ->
                        priceUpdatesFlow.value = symbolPricePair
                    }
                    .collect()
            }
        }
    }

    override fun disconnect() {
        temporarySubscriptions.clear()
        portfolioSubscriptions.clear()
        session?.cancel()
    }

    override fun priceUpdateReceived(): Flow<SymbolPricePair> = priceUpdatesFlow.drop(1)

    override fun setPortfolioSubscriptions(
        symbols: Collection<String>,
        newCurrency: String,
        oldCurrency: String
    ) {
        val symbolsToUnsubscribe =
            symbols.plus(temporarySubscriptions)// portfolioSubscriptions.minus(symbols).minus(temporarySubscriptions)
        val symbolsToSubscribe = symbols.minus(portfolioSubscriptions).minus(temporarySubscriptions)
        portfolioSubscriptions.clear()
        portfolioSubscriptions.addAll(symbols)
        removeSubscriptions(symbolsToUnsubscribe, oldCurrency)
        addSubscriptions(symbols.toList(), newCurrency)
        this.currency = newCurrency
    }

    override fun addTemporarySubscription(symbol: String, currency: String) {
        temporarySubscriptions.add(symbol)
        if (!portfolioSubscriptions.contains(symbol)) {
            addSubscriptions(listOf(symbol), currency)
        }
        this.currency = currency
    }

    override fun clearTemporarySubscriptions(currency: String) {
        val symbolsToUnsubscribe = temporarySubscriptions.minus(portfolioSubscriptions)
        temporarySubscriptions.clear()
        removeSubscriptions(symbolsToUnsubscribe, currency)
    }

    private fun addSubscriptions(symbols: List<String>, currency: String): Boolean {
        if (symbols.isNotEmpty()) {
            val subs = symbols.map { "5~CCCAGG~$it~$currency" }
            val subscription = WebSocketSubscription(
                action = "SubAdd",
                subs = subs
            )
            scope.launch {
                session?.send(
                    Frame.byType(
                        true,
                        FrameType.TEXT,
                        Json.encodeToString(subscription)
                            .encodeToByteArray()
                    )
                )
            }
            return true
        }
        return false
    }

    private fun removeSubscriptions(symbols: Collection<String>, currency: String): Boolean {
        if (symbols.isNotEmpty()) {
            val subscription = WebSocketSubscription(
                action = "SubRemove",
                subs = symbols.map { "5~CCCAGG~$it~$currency" }
            )
            scope.launch {
                session?.send(
                    Frame.byType(
                        true,
                        FrameType.TEXT,
                        Json.encodeToString(subscription)
                            .encodeToByteArray()
                    )
                )
            }
            return true
        }
        return false
    }
}
