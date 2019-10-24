package com.charliechristensen.cryptotracker.common

import android.util.Log
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.websocket.WebSocketService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class LiveUpdatePriceClient @Inject constructor(
    private val appPreferences: AppPreferences,
    private val repository: Repository,
    private val webSocketService: WebSocketService
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun start() {

        appPreferences.liveUpdatePrices()
            .flatMapLatest { isSet ->
                if (isSet) {
                    repository.getPortfolioCoinSymbols()
                } else {
                    flow {
                        webSocketService.disconnect()
                    }
                }
            }
            .onEach { symbolsList ->
                webSocketService.connect { socket ->
                    socket.setPortfolioSubscriptions(symbolsList, Constants.MyCurrency)
                }
            }
            .onCompletion { webSocketService.disconnect() }
            .catch { Log.d("SOCKET IO ERROR", it.localizedMessage) }
            .launchIn(scope)

        webSocketService.priceUpdateRecieveds()
            .onEach { repository.updatePriceForCoin(it.symbol, it.price) }
            .launchIn(scope)
    }

    fun stop() {
        scope.coroutineContext.cancelChildren()
    }

}
