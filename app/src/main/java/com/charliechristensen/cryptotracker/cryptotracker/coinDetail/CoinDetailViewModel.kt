package com.charliechristensen.cryptotracker.cryptotracker.coinDetail

import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryGraphEntry
import com.charliechristensen.network.socketio.WebSocketService
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.Observable.combineLatest
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for coin details
 */
interface CoinDetailViewModel {

    interface Inputs {
        fun graphDateSelectionChanged(index: Int)
        fun addCoinButtonClicked()
        fun editQuantityButtonClicked()
        fun removeFromPortfolioButtonClicked()
        fun confirmAddCoinToPortfolioClicked(symbol: String, amount: Double)
        fun confirmEditCoinAmountClicked(symbol: String, amount: Double)
        fun confirmRemoveCoinFromPortfolioClicked(symbol: String)
    }

    interface Outputs {
        fun toolbarImageData(): Observable<ImageAndNamePair>
        fun currentCoinPrice(): Observable<String>
        fun low24Hour(): Observable<String>
        fun high24Hour(): Observable<String>
        fun isCoinInPortfolio(): Observable<Boolean>
        fun percentChangeTimePeriod(): Observable<Int>
        fun valueChange24Hour(): Observable<ColorValueString>
        fun percentChange24Hour(): Observable<ColorValueString>
        fun walletUnitsOwned(): Observable<String>
        fun walletTotalValue(): Observable<String>
        fun walletPriceChange24Hour(): Observable<ColorValueString>
        fun graphState(): Observable<CoinDetailGraphState>
        fun showAddCoinDialog(): Observable<String>
        fun showEditCoinAmountDialog(): Observable<String>
        fun showConfirmRemoveDialog(): Observable<String>
        fun showNetworkError(): Observable<Unit>
    }

    class ViewModel @AssistedInject constructor(
        private val repository: Repository,
        private val webSocket: WebSocketService,
        private val formatterFactory: FormatterFactory,
        @Assisted val coinSymbol: String
    ) : BaseViewModel(), Inputs, Outputs {

        private val coinIsInPortfolioRelay = BehaviorRelay.create<Boolean>()
        private val currentPricePerUnitRelay = BehaviorRelay.createDefault(0.0)
        private val pricePerUnit24HourLowRelay = BehaviorRelay.createDefault(0.0)
        private val pricePerUnit24HourHighRelay = BehaviorRelay.createDefault(0.0)
        private val walletUnitsOwnedRelay = BehaviorRelay.createDefault(0.0)
        private val walletTotalValueRelay = BehaviorRelay.createDefault(0.0)
        private val walletPriceChange24HourRelay = BehaviorRelay.createDefault(
            ColorValueString.create(
                0.0,
                formatterFactory.currencyFormatter()
            )
        )
        private val toolbarImageDataRelay = BehaviorRelay.createDefault(ImageAndNamePair())
        private val currentStartPricePerUnitRelay = BehaviorRelay.createDefault(0.0)
        private val currentTimePeriodRelay: BehaviorRelay<CoinHistoryTimePeriod> =
            BehaviorRelay.createDefault(
                CoinHistoryTimePeriod.OneDay
            )
        private val graphStateRelay: BehaviorRelay<CoinDetailGraphState> =
            BehaviorRelay.createDefault(CoinDetailGraphState.Loading)

        private val showAddCoinDialogRelay: PublishRelay<String> = PublishRelay.create()
        private val showEditQuantityDialogRelay: PublishRelay<String> = PublishRelay.create()
        private val showConfirmRemoveCoinDialogRelay: PublishRelay<String> = PublishRelay.create()
        private val showNetworkErrorRelay: PublishRelay<Unit> = PublishRelay.create()

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            getCoinData(coinSymbol, repository)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = {
                        coinIsInPortfolioRelay.accept(it.coinIsInPortfolio)
                        currentPricePerUnitRelay.accept(it.pricePerUnit)
                        pricePerUnit24HourLowRelay.accept(it.pricePerUnit24HourLow)
                        pricePerUnit24HourHighRelay.accept(it.pricePerUnit24HourHigh)
                        walletUnitsOwnedRelay.accept(it.walletUnitsOwned)
                        walletTotalValueRelay.accept(it.walletTotalValue)
                        walletPriceChange24HourRelay.accept(
                            ColorValueString.create(
                                it.walletPriceChange24Hour,
                                formatterFactory.currencyFormatter()
                            )
                        )
                        toolbarImageDataRelay.accept(
                            ImageAndNamePair(
                                it.coinName,
                                it.imageUrl
                            )
                        )
                    },
                    onError = {
                        showNetworkErrorRelay.accept(Unit)
                    }
                )
                .addTo(disposables)

            webSocket.addTemporarySubscription(coinSymbol, Constants.MyCurrency)

            currentTimePeriodRelay
                .doOnNext { graphStateRelay.accept(CoinDetailGraphState.Loading) }
                .switchMapSingle { getCoinGraphData(coinSymbol, it, repository) }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = {
                        graphStateRelay.accept(it)
                        if (it is CoinDetailGraphState.Success) {
                            currentStartPricePerUnitRelay.accept(it.startingPrice)
                        }
                    },
                    onError = {
                        graphStateRelay.accept(CoinDetailGraphState.Error)
                    }
                )
                .addTo(disposables)
        }

        override fun onCleared() {
            super.onCleared()
            webSocket.clearTemporarySubscriptions(Constants.MyCurrency)
        }

        //region Inputs

        override fun graphDateSelectionChanged(index: Int) {
            currentTimePeriodRelay.accept(CoinHistoryTimePeriod.getTimePeriodWithIndex(index))
        }

        override fun addCoinButtonClicked() {
            showAddCoinDialogRelay.accept(coinSymbol)
        }

        override fun editQuantityButtonClicked() {
            showEditQuantityDialogRelay.accept(coinSymbol)
        }

        override fun removeFromPortfolioButtonClicked() {
            showConfirmRemoveCoinDialogRelay.accept(coinSymbol)
        }

        override fun confirmAddCoinToPortfolioClicked(symbol: String, amount: Double) {
            saveCoinToPortfolio(symbol, amount, repository)
        }

        override fun confirmEditCoinAmountClicked(symbol: String, amount: Double) {
            saveCoinToPortfolio(symbol, amount, repository)
        }

        override fun confirmRemoveCoinFromPortfolioClicked(symbol: String) {
            removeCoinFromPortfolio(symbol, repository)
        }

        //endregion

        //region Outputs

        override fun toolbarImageData(): Observable<ImageAndNamePair> =
            toolbarImageDataRelay.distinctUntilChanged()

        override fun currentCoinPrice(): Observable<String> =
            currentPricePerUnitRelay.distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun low24Hour(): Observable<String> =
            pricePerUnit24HourLowRelay.distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun high24Hour(): Observable<String> =
            pricePerUnit24HourHighRelay.distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun isCoinInPortfolio(): Observable<Boolean> =
            coinIsInPortfolioRelay.distinctUntilChanged()

        override fun percentChangeTimePeriod(): Observable<Int> =
            currentTimePeriodRelay.distinctUntilChanged()
                .map { it.displayString }

        override fun valueChange24Hour(): Observable<ColorValueString> = combineLatest(
            currentPricePerUnitRelay,
            currentStartPricePerUnitRelay,
            BiFunction<Double, Double, ColorValueString> { price: Double, startPrice: Double ->
                val valueChangeDouble = price - startPrice
                ColorValueString.create(valueChangeDouble, formatterFactory.currencyFormatter())
            })
            .distinctUntilChanged()

        override fun percentChange24Hour(): Observable<ColorValueString> = combineLatest(
            currentPricePerUnitRelay,
            currentStartPricePerUnitRelay,
            BiFunction<Double, Double, ColorValueString> { price: Double, startPrice: Double ->
                val percentChange = if (startPrice > 0.0) {
                    ((price - startPrice) / startPrice)
                } else {
                    0.0
                }
                ColorValueString.create(percentChange, formatterFactory.percentFormatter())
            })
            .distinctUntilChanged()

        override fun walletUnitsOwned(): Observable<String> =
            walletUnitsOwnedRelay.distinctUntilChanged()
                .map { formatterFactory.decimalFormatter().format(it) }

        override fun walletTotalValue(): Observable<String> =
            walletTotalValueRelay.distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun walletPriceChange24Hour(): Observable<ColorValueString> =
            walletPriceChange24HourRelay.distinctUntilChanged()

        override fun graphState(): Observable<CoinDetailGraphState> =
            graphStateRelay.distinctUntilChanged()

        override fun showAddCoinDialog(): Observable<String> =
            showAddCoinDialogRelay

        override fun showEditCoinAmountDialog(): Observable<String> =
            showEditQuantityDialogRelay

        override fun showConfirmRemoveDialog(): Observable<String> =
            showConfirmRemoveCoinDialogRelay

        override fun showNetworkError(): Observable<Unit> =
            showNetworkErrorRelay

        //endregion

        //region Repository Calls

        private fun getCoinData(
            coinSymbol: String,
            repository: Repository
        ): Observable<PriceDataCombined> = Observables.combineLatest(
            repository.getCoinPriceData(coinSymbol),
            repository.getUnitsOwnedForSymbol(coinSymbol),
            repository.getCoinDetails(coinSymbol)
        ).map { (priceList, unitsOwnedList, dbCoinList) ->
            val coinName = dbCoinList.getOrNull(0)?.coinName ?: ""
            val imageUrl = dbCoinList.getOrNull(0)?.imageUrl ?: ""
            val unitsOwned = unitsOwnedList.getOrElse(0) { 0.0 }
            val coinIsInPortfolio = unitsOwnedList.isNotEmpty()
            val priceData = priceList.getOrElse(0) {
                return@map PriceDataCombined(
                    coinName,
                    imageUrl,
                    coinIsInPortfolio,
                    unitsOwned
                )
            }
            val walletTotalValueDouble = priceData.price * unitsOwned
            val walletTotalValueOpenDouble = priceData.open24Hour * unitsOwned
            val walletTotalValueChangeDouble =
                walletTotalValueDouble - walletTotalValueOpenDouble
            PriceDataCombined(
                coinName,
                imageUrl,
                coinIsInPortfolio,
                unitsOwned,
                priceData.price,
                priceData.low24Hour,
                priceData.high24Hour,
                walletTotalValueDouble,
                walletTotalValueChangeDouble
            )
        }

    private fun getCoinGraphData(
        coinSymbol: String,
        timePeriod: CoinHistoryTimePeriod,
        repository: Repository
    ): Single<CoinDetailGraphState> {
        return repository.getHistoricalDataForCoin(coinSymbol, timePeriod)
            .subscribeOn(Schedulers.io())
            .map {
                var color = ValueChangeColor.GREEN
                var validStartPrice = false
                var startPrice = 0.0
                val endPrice = it.historyElements.lastOrNull()?.close ?: 0.0
                val list = it.historyElements.map { coinHistoryElement: CoinHistoryElement ->
                    val closingPrice = coinHistoryElement.close
                    if (!validStartPrice) {
                        if (closingPrice > 0.0) {
                            validStartPrice = true
                            if (closingPrice > endPrice) {
                                color = ValueChangeColor.RED
                            }
                            startPrice = closingPrice
                        }
                    }
                    CoinHistoryGraphEntry(
                        coinHistoryElement.time.toFloat(),
                        closingPrice.toFloat()
                    )
                }
                if (list.isEmpty()) {
                    return@map CoinDetailGraphState.NoData
                } else {
                    return@map CoinDetailGraphState.Success(list, color, startPrice)
                }
            }
    }

    private fun saveCoinToPortfolio(symbol: String, amount: Double, repository: Repository) {
        repository.addPortfolioCoin(symbol, amount)
            .subscribeOn(Schedulers.io())
            .subscribe()
            .addTo(disposables)
    }

    private fun removeCoinFromPortfolio(symbol: String, repository: Repository) {
        repository.removeCoinFromPortfolio(symbol)
            .subscribeOn(Schedulers.io())
            .subscribe()
            .addTo(disposables)
    }

    //endregion

    private data class PriceDataCombined(
        val coinName: String = "",
        val imageUrl: String? = "",
        val coinIsInPortfolio: Boolean = false,
        val walletUnitsOwned: Double = 0.0,
        val pricePerUnit: Double = 0.0,
        val pricePerUnit24HourLow: Double = 0.0,
        val pricePerUnit24HourHigh: Double = 0.0,
        val walletTotalValue: Double = 0.0,
        val walletPriceChange24Hour: Double = 0.0
    )

    @AssistedInject.Factory
    interface Factory {
        fun create(coinSymbol: String): ViewModel
    }

}
}