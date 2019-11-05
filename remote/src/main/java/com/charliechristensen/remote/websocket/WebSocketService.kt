package com.charliechristensen.remote.websocket

import com.charliechristensen.remote.models.SymbolPricePair
import kotlinx.coroutines.flow.Flow

interface WebSocketService {
    fun connect(onConnection: (WebSocketService) -> Unit)
    fun disconnect()
    fun priceUpdateReceived(): Flow<SymbolPricePair>
    fun setPortfolioSubscriptions(symbols: Collection<String>, currency: String)
    fun addTemporarySubscription(symbol: String, currency: String)
    fun clearTemporarySubscriptions(currency: String)
}
