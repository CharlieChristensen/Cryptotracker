package com.charliechristensen.remote.websocketv2

import com.charliechristensen.remote.models.WebSocketCoinData
import com.charliechristensen.remote.models.WebSocketSubscription
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface SocketService {

    @Send
    fun subscribe(subscription: WebSocketSubscription)

    @Receive
    fun observeCoinData(): Flow<WebSocketCoinData>

    @Receive
    fun observeWebsocketEvent(): Flow<WebSocket.Event>

    @Receive
    fun observeWebsocketConnect(): Flow<WebSocket.Event>

}
