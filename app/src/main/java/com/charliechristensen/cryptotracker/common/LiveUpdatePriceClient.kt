package com.charliechristensen.cryptotracker.common

import com.charliechristensen.cryptotracker.common.extensions.accumulate
import com.charliechristensen.cryptotracker.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class LiveUpdatePriceClient constructor(
    private val repository: Repository
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    @ExperimentalCoroutinesApi
    fun start() {
        combine(
            liveUpdatePrices(),
            repository.currency().accumulate(repository.getCurrency())
        )
        { symbolsList, (previousCurrency, nextCurrency) ->
//            repository.connectToLivePrices(symbolsList, previousCurrency, nextCurrency)
            repository.setPortfolioSubscriptions(symbolsList, previousCurrency, nextCurrency)
        }
            .onCompletion { repository.disconnectFromLivePrices() }
            .catch { Timber.tag("SOCKET IO ERROR").d(it.localizedMessage ?: "UNKNOWN ERROR") }
            .launchIn(scope)

        repository.priceUpdateReceived()
            .onEach { repository.updatePriceForCoin(it.symbol, it.currency, it.price) }
            .launchIn(scope)
    }

    fun stop() {
        scope.coroutineContext.cancelChildren()
    }

    @ExperimentalCoroutinesApi
    private fun liveUpdatePrices(): Flow<List<String>> = repository.liveUpdatePrices()
        .flatMapLatest { isSet ->
            if (isSet) {
                repository.getPortfolioCoinSymbols()
            } else {
                flow {
                    repository.disconnectFromLivePrices()
                }
            }
        }

}
