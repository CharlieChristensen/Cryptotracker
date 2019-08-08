package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import com.charliechristensen.cryptotracker.common.AppPreferences
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface MainActivityViewModel {

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
        private val liveUpdatePriceClient: LiveUpdatePriceClient,
        private val appPreferences: AppPreferences,
        repository: Repository
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

            repository.refreshCoinListIfNeeded()
                .subscribeOn(Schedulers.io())
                .subscribe()
                .addTo(disposables)

            liveUpdatePriceClient.start()
        }

        override fun onCleared() {
            super.onCleared()
            liveUpdatePriceClient.stop()
        }

        //region Inputs

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

        //endregion

        //region Outputs

        override fun getAppThemeSync() = appPreferences.getTheme()

        override fun theme(): Observable<AppTheme> = themeRelay

        override fun navigateToPortfolio(): Observable<Unit> = navigateToPortfolioRelay

        override fun navigateToSearchCoins(): Observable<Unit> = navigateToSearchCoinsRelay

        override fun navigateToSettings(): Observable<Unit> = navigateToSettingsRelay

        //endregion

    }

}