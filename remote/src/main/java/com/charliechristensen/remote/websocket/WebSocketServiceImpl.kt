package com.charliechristensen.remote.websocket

import com.charliechristensen.remote.models.SymbolPricePair
import com.charliechristensen.remote.models.WebSocketSubscription
import com.charliechristensen.remote.websocketv2.SocketService
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WebSocketServiceImpl @Inject constructor(
    @Named("WebSocketUrl") url: String,
    private val socketService: SocketService
) : WebSocketService {

    private var isConnected = false

    private var currency: String? = null
    private val temporarySubscriptions: MutableSet<String> = mutableSetOf()
    private val portfolioSubscriptions: MutableSet<String> = mutableSetOf()

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    init {
        socketService.observeWebsocketConnect()
            .flowOn(Dispatchers.IO)
            .onEach { event ->
                Timber.d("Event Received! $event")
                if (event is WebSocket.Event.OnConnectionOpened<*>) {
                    isConnected = true
                    val currency = currency
                    if (!currency.isNullOrBlank()) {
                        addSubscriptionsV2(
                            temporarySubscriptions.plus(portfolioSubscriptions).toList(),
                            currency
                        )
                    }
                } else if (event is WebSocket.Event.OnConnectionClosed || event is WebSocket.Event.OnConnectionFailed) {
                    isConnected = false
                }
            }
            .launchIn(scope)
    }

    override fun connect(): Flow<WebSocketService> {
        return socketService.observeWebsocketEvent()
            .flowOn(Dispatchers.IO)
            .onEach { event ->
                when (event) {
                    is WebSocket.Event.OnConnectionOpened<*>
                    -> isConnected = true
                    is WebSocket.Event.OnConnectionClosed,
                    is WebSocket.Event.OnConnectionFailed
                    -> isConnected = false
                }
            }
            .filter {
                it is WebSocket.Event.OnConnectionOpened<*>
            }
            .map { this }
    }

    override fun disconnect() {
        temporarySubscriptions.clear()
        portfolioSubscriptions.clear()
    }

    override fun priceUpdateReceived(): Flow<SymbolPricePair> = socketService.observeCoinData()
        .flowOn(Dispatchers.IO)
        .filter { it.type == 5 }
        .mapNotNull { coinData ->
            SymbolPricePair(
                coinData.fromSymbol ?: return@mapNotNull null,
                coinData.toSymbol ?: return@mapNotNull null,
                coinData.price ?: return@mapNotNull null
            )
        }

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
        removeSubscriptionsV2(symbolsToUnsubscribe, oldCurrency)
        addSubscriptionsV2(symbols.toList(), newCurrency)
        this.currency = newCurrency
    }

    override fun addTemporarySubscription(symbol: String, currency: String) {
        temporarySubscriptions.add(symbol)
        if (!portfolioSubscriptions.contains(symbol)) {
            addSubscriptionsV2(listOf(symbol), currency)
        }
        this.currency = currency
    }

    override fun clearTemporarySubscriptions(currency: String) {
        val symbolsToUnsubscribe = temporarySubscriptions.minus(portfolioSubscriptions)
        temporarySubscriptions.clear()
        removeSubscriptionsV2(symbolsToUnsubscribe, currency)
    }

    private fun addSubscriptionsV2(symbols: List<String>, currency: String): Boolean {
        if (isConnected && symbols.isNotEmpty()) {
            val subs = symbols.map { "5~CCCAGG~$it~$currency" }
            Timber.d("Adding symbols $subs")
            socketService.subscribe(
                WebSocketSubscription(
                    action = "SubAdd",
                    subs = subs
                )
            )
            return true
        }
        return false
    }

    private fun removeSubscriptionsV2(symbols: Collection<String>, currency: String): Boolean {
        if (isConnected && symbols.isNotEmpty()) {
            Timber.d("Removing symbols $symbols")
            socketService.subscribe(
                WebSocketSubscription(
                    action = "SubRemove",
                    subs = symbols.map { "5~CCCAGG~$it~$currency" }
                )
            )
            return true
        }
        return false
    }
}
