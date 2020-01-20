package com.charliechristensen.remote

import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService

interface RemoteGateway: CryptoService, WebSocketService
