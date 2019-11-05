package com.charliechristensen.cryptotracker.common

import android.util.Log
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class LiveUpdatePriceClient @Inject constructor(
    private val appPreferences: AppPreferences,
    private val repository: Repository
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun start() {

        appPreferences.liveUpdatePrices()
            .flatMapLatest { isSet ->
                if (isSet) {
                    repository.getPortfolioCoinSymbols()
                } else {
                    flow {
                        repository.disconnectFromLivePrices()
                    }
                }
            }
            .onEach { symbolsList ->
                repository.connectToLivePrices(symbolsList, Constants.MyCurrency)
            }
            .onCompletion { repository.disconnectFromLivePrices() }
            .catch { Log.d("SOCKET IO ERROR", it.localizedMessage ?: "UNKNOWN ERROR") }
            .launchIn(scope)

        repository.priceUpdateReceived()
            .onEach { repository.updatePriceForCoin(it.symbol, it.price) }
            .launchIn(scope)

    }

    fun stop() {
        scope.coroutineContext.cancelChildren()
    }

}
