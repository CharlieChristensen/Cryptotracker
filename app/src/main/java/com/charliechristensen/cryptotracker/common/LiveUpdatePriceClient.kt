package com.charliechristensen.cryptotracker.common

import android.util.Log
import com.charliechristensen.cryptotracker.data.Repository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
@Singleton
class LiveUpdatePriceClient @Inject constructor(
    private val repository: Repository
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun start() {
        repository.liveUpdatePrices()
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
                repository.connectToLivePrices(symbolsList, repository.getCurrency())
            }
            .onCompletion { repository.disconnectFromLivePrices() }
            .catch { Log.d("SOCKET IO ERROR", it.localizedMessage ?: "UNKNOWN ERROR") }
            .launchIn(scope)

        repository.priceUpdateReceived()
            .onEach { repository.updatePriceForCoin(it.symbol, it.currency, it.price) }
            .launchIn(scope)
    }

    fun stop() {
        scope.coroutineContext.cancelChildren()
    }
}
