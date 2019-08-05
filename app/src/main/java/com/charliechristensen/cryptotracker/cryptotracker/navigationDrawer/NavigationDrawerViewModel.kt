package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.util.Log
import com.charliechristensen.cryptotracker.common.*
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.network.socketio.WebSocketService
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Chuck on 1/22/2018.
 */
interface NavigationDrawerViewModel {

    interface Inputs {
        fun navigationItemSelected(itemId: Int)
    }

    interface Outputs {
        fun navigateToPortfolio(): Observable<Unit>

        fun navigateToSearchCoins(): Observable<Unit>

        fun navigateToSettings(): Observable<Unit>

        fun theme(): Observable<AppTheme>

        fun getAppThemeSync(): AppTheme
    }

    class ViewModel @Inject constructor(
        private val appPreferences: AppPreferences,
        private val repository: Repository,
        private val webSocket: WebSocketService
    ) : BaseViewModel(), Inputs, Outputs {

        private val themeRelay = PublishRelay.create<AppTheme>()

        private val navigateToPortfolioRelay = PublishRelay.create<Unit>()
        private val navigateToSearchCoinsRelay = PublishRelay.create<Unit>()
        private val navigateToSettingsRelay = PublishRelay.create<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            appPreferences.theme()
                .subscribeOn(Schedulers.io())
                .subscribe(themeRelay)
                .addTo(disposables)

//            appPreferences.liveUpdatePrices()
//                .switchMap {
//                    if (it) {
//                        repository.getPortfolioCoinSymbols()
//                    } else {
//                        webSocket.disconnect()
//                        Observable.empty<List<String>>()
//                    }
//                }
//                .subscribeOn(Schedulers.io())
//                .subscribeBy(onNext = { symbolsList ->
//                    webSocket.connect { socket ->
//                        socket.setPortfolioSubscriptions(symbolsList, Constants.MyCurrency)
//                    }
//                }, onError = {
//                    Log.d("SOCKET IO ERROR", it.localizedMessage)
//                })
//                .addTo(disposables)

            webSocket.priceUpdateReceived()
                .observeOn(Schedulers.io())
                .subscribeBy { repository.updatePriceForCoin(it.symbol, it.price) }
                .addTo(disposables)

        }

        override fun onCleared() {
            super.onCleared()
            webSocket.disconnect()
        }

        override fun getAppThemeSync() =
            appPreferences.getTheme()

        override fun theme(): Observable<AppTheme> =
            themeRelay

        override fun navigateToPortfolio(): Observable<Unit> =
            navigateToPortfolioRelay

        override fun navigateToSearchCoins(): Observable<Unit> =
            navigateToSearchCoinsRelay

        override fun navigateToSettings(): Observable<Unit> =
            navigateToSettingsRelay

        override fun navigationItemSelected(itemId: Int) {
            when (itemId) {
                R.id.nav_my_portfolio -> {
                    navigateToPortfolioRelay.accept(Unit)
                }
                R.id.nav_coin_prices -> {
                    navigateToSearchCoinsRelay.accept(Unit)
                }
                R.id.nav_settings -> {
                    navigateToSettingsRelay.accept(Unit)
                }
            }
        }

    }

}