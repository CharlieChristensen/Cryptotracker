package com.charliechristensen.remote

import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService

class RemoteGatewayImpl(
    private val cryptoService: CryptoService,
    private val webSocketService: WebSocketService
): RemoteGateway, CryptoService by cryptoService, WebSocketService by webSocketService
