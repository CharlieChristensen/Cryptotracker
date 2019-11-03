package com.charliechristensen.cryptotracker.data.websocket

import com.charliechristensen.cryptotracker.data.models.network.SymbolPricePair
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.json.JSONArray
import org.json.JSONObject

@FlowPreview
@ExperimentalCoroutinesApi
class WebSocketService(url: String) {

    private val webSocket: Socket = IO.socket(url)
    private val temporarySubscriptions: MutableSet<String> = mutableSetOf()
    private val portfolioSubscriptions: MutableSet<String> = mutableSetOf()
    private val priceUpdateReceivedChannel = BroadcastChannel<SymbolPricePair>(1)
    private val errorConnectingToSocketChannel = BroadcastChannel<Unit>(1)

    init {
        webSocket.on("m"){
            val socketResponse = SocketResponseMapper.mapResponse(it) ?: return@on
            if (socketResponse.subscriptionId == 5) {
                if (socketResponse.priceDirection == 1 || socketResponse.priceDirection == 2) {
                    priceUpdateReceivedChannel.offer(
                        SymbolPricePair(
                            socketResponse.fromCurrency,
                            socketResponse.price
                        )
                    )
                }
            }
        }
    }

    fun connect(onConnection: (WebSocketService) -> Unit){
        if(!webSocket.connected()){
            webSocket.connect()
            webSocket.on(Socket.EVENT_CONNECT){
                onConnection.invoke(this)
            }
            webSocket.on(Socket.EVENT_ERROR) {
                errorConnectingToSocketChannel.offer(Unit)
            }
        }else{
            onConnection.invoke(this)
        }
    }

    fun disconnect(){
        temporarySubscriptions.clear()
        portfolioSubscriptions.clear()
        webSocket.disconnect()
    }

    fun priceUpdateReceived(): Flow<SymbolPricePair> =
        priceUpdateReceivedChannel.asFlow()

    fun setPortfolioSubscriptions(symbols: Collection<String>, currency: String){
        val symbolsToUnsubscribe = portfolioSubscriptions.minus(symbols).minus(temporarySubscriptions)
        val symbolsToSubscribe = symbols.minus(portfolioSubscriptions).minus(temporarySubscriptions)
        portfolioSubscriptions.clear()
        portfolioSubscriptions.addAll(symbols)
        removeSubscriptions(symbolsToUnsubscribe, currency)
        addSubscriptions(symbolsToSubscribe, currency)
    }

    fun addTemporarySubscription(symbol: String, currency: String){
        temporarySubscriptions.add(symbol)
        if(!portfolioSubscriptions.contains(symbol)) {
            addSubscriptions(listOf(symbol), currency)
        }
    }

    fun clearTemporarySubscriptions(currency: String){
        val symbolsToUnsubscribe = temporarySubscriptions.minus(portfolioSubscriptions)
        temporarySubscriptions.clear()
        removeSubscriptions(symbolsToUnsubscribe, currency)
    }

    private fun addSubscriptions(symbols: List<String>, currency: String): Boolean{
        if(webSocket.connected() && symbols.isNotEmpty()) {
            try {
                val jsonArray = JSONArray()
                symbols.forEach {
                    jsonArray.put("5~CCCAGG~$it~$currency")
                }
                val jsonParams = JSONObject()
                jsonParams.put("subs", jsonArray)
                webSocket.emit("SubAdd", jsonParams)
                return true
            } catch (e: Exception) {
                //JSON Exception
            }
        }
        return false
    }

    private fun removeSubscriptions(symbols: Iterable<String>, currency: String): Boolean{
        if(webSocket.connected()) {
            try {
                val jsonArray = JSONArray()
                symbols.forEach {
                    jsonArray.put("5~CCCAGG~$it~$currency")
                }
                val jsonParams = JSONObject()
                jsonParams.put("subs", jsonArray)
                webSocket.emit("SubRemove", jsonParams)
                return true
            } catch (e: Exception) {
                //JSON Exception
            }
        }
        return false
    }
}
