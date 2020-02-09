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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.scanReduce

@ExperimentalCoroutinesApi
@Singleton
class LiveUpdatePriceClient @Inject constructor(
    private val repository: Repository
) {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun start() {
        combine(
            liveUpdatePrices(),
            repository.currency().accumulate(repository.getCurrency())
        )
        { symbolsList, (previousCurrency, nextCurrency) ->
            repository.connectToLivePrices(symbolsList, previousCurrency, nextCurrency)
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

    inline fun <T> Flow<T>.accumulate(
        initial: T
    ): Flow<Pair<T, T>> = flow {
        var previous: T = initial
        emit(previous to previous)
        collect { next ->
            emit(previous to next)
            previous = next
        }
    }

}
