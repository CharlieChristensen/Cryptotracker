package com.charliechristensen.cryptotracker.common

import android.util.Log
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.network.socketio.WebSocketService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveUpdatePriceClient @Inject constructor(
    private val appPreferences: AppPreferences,
    private val repository: Repository,
    private val webSocketService: WebSocketService
) {

    private val disposables = CompositeDisposable()

    fun start() {
        appPreferences.liveUpdatePrices()
            .switchMap {
                if (it) {
                    repository.getPortfolioCoinSymbols()
                } else {
                    webSocketService.disconnect()
                    Observable.empty<List<String>>()
                }
            }
            .doFinally { webSocketService.disconnect() }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { symbolsList ->
                    webSocketService.connect { socket ->
                        socket.setPortfolioSubscriptions(symbolsList, Constants.MyCurrency)
                    }
                },
                onError = {
                    Log.d("SOCKET IO ERROR", it.localizedMessage)
                }
            )
            .addTo(disposables)

        webSocketService.priceUpdateReceived()
            .observeOn(Schedulers.io())
            .subscribeBy { repository.updatePriceForCoin(it.symbol, it.price) }
            .addTo(disposables)
    }

    fun stop() {
        disposables.clear()
    }

}