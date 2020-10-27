package com.charliechristensen.coindetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

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
        val toolbarImageData: Flow<ImageAndNamePair>
        val currentCoinPrice: Flow<String>
        val low24Hour: Flow<String>
        val high24Hour: Flow<String>
        val isCoinInPortfolio: Flow<Boolean>
        val percentChangeTimePeriod: Flow<Int>
        val valueChange24Hour: Flow<ColorValueString>
        val percentChange24Hour: Flow<ColorValueString>
        val walletUnitsOwned: Flow<String>
        val walletTotalValue: Flow<String>
        val walletPriceChange24Hour: Flow<ColorValueString>
        val graphState: Flow<CoinDetailGraphState>
        val showAddCoinDialog: Flow<String>
        val showEditCoinAmountDialog: Flow<String>
        val showConfirmRemoveDialog: Flow<String>
        val showNetworkError: Flow<Unit>
        val selectedDateTab: Flow<Int>
    }

    class ViewModel constructor(
        private val formatterFactory: FormatterFactory,
        private val interactor: CoinDetailInteractor,
        private val coinSymbol: String,
        private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

        private val currencyFormatter = formatterFactory.currencyFormatter(interactor.getCurrency())
        private val coinIsInPortfolioChannel = MutableStateFlow(false)
        private val currentPricePerUnitChannel = MutableStateFlow(0.0)
        private val pricePerUnit24HourLowChannel = MutableStateFlow(0.0)
        private val pricePerUnit24HourHighChannel = MutableStateFlow(0.0)
        private val walletUnitsOwnedChannel = MutableStateFlow(0.0)
        private val walletTotalValueChannel = MutableStateFlow(0.0)
        private val walletPriceChange24HourChannel = MutableStateFlow(
            ColorValueString.create(0.0, currencyFormatter)
        )
        private val toolbarImageDataChannel = MutableStateFlow(ImageAndNamePair())
        private val currentStartPricePerUnitChannel = MutableStateFlow(0.0)
        private val currentTimePeriodChannel: MutableStateFlow<CoinHistoryTimePeriod> =
            MutableStateFlow(
                CoinHistoryTimePeriod.getTimePeriodWithIndex(
                    savedState.get<Int>(KEY_GRAPH_DATE_SELECTION) ?: 0
                )
            )
        private val showAddCoinDialogChannel = MutableSharedFlow<String>()
        private val showEditQuantityDialogChannel = MutableSharedFlow<String>()
        private val showConfirmRemoveCoinDialogChannel = MutableSharedFlow<String>()
        private val showNetworkErrorChannel = MutableSharedFlow<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            interactor.getCoinData(coinSymbol)
                .onEach { priceData ->
                    coinIsInPortfolioChannel.value = priceData.coinIsInPortfolio
                    currentPricePerUnitChannel.value = priceData.pricePerUnit
                    pricePerUnit24HourLowChannel.value = priceData.pricePerUnit24HourLow
                    pricePerUnit24HourHighChannel.value = priceData.pricePerUnit24HourHigh
                    walletUnitsOwnedChannel.value = priceData.walletUnitsOwned
                    walletTotalValueChannel.value = priceData.walletTotalValue
                    walletPriceChange24HourChannel.value = priceData.walletPriceChange24Hour
                    toolbarImageDataChannel.value = priceData.toolbarImageData
                }
                .catch {
                    Timber.e(it, "Get Coin Data")
                    showNetworkErrorChannel.tryEmit(Unit)
                }
                .launchIn(viewModelScope)

            interactor.addTemporarySubscription(coinSymbol)
        }

        override fun onCleared() {
            super.onCleared()
            interactor.clearTemporarySubscriptions()
        }

        //region Inputs

        override fun graphDateSelectionChanged(index: Int) {
            savedState.set(KEY_GRAPH_DATE_SELECTION, index)
            currentTimePeriodChannel.value = CoinHistoryTimePeriod.getTimePeriodWithIndex(index)
        }

        override fun addCoinButtonClicked() {
            viewModelScope.launch {
                showAddCoinDialogChannel.emit(coinSymbol)
            }
        }

        override fun editQuantityButtonClicked() {
            viewModelScope.launch {
                showEditQuantityDialogChannel.emit(coinSymbol)
            }
        }

        override fun removeFromPortfolioButtonClicked() {
            viewModelScope.launch {
                showConfirmRemoveCoinDialogChannel.emit(coinSymbol)
            }
        }

        override fun confirmAddCoinToPortfolioClicked(symbol: String, amount: Double) {
            saveCoinToPortfolio(symbol, amount)
        }

        override fun confirmEditCoinAmountClicked(symbol: String, amount: Double) {
            saveCoinToPortfolio(symbol, amount)
        }

        override fun confirmRemoveCoinFromPortfolioClicked(symbol: String) {
            removeCoinFromPortfolio(symbol)
        }

        //endregion

        //region Outputs

        override val toolbarImageData: Flow<ImageAndNamePair> = toolbarImageDataChannel

        override val currentCoinPrice: Flow<String> = currentPricePerUnitChannel
            .map { currencyFormatter.format(it) }
            .share()

        override val low24Hour: Flow<String> = pricePerUnit24HourLowChannel
            .map { currencyFormatter.format(it) }
            .share()

        override val high24Hour: Flow<String> = pricePerUnit24HourHighChannel
            .map { currencyFormatter.format(it) }
            .share()

        override val isCoinInPortfolio: Flow<Boolean> = coinIsInPortfolioChannel

        override val percentChangeTimePeriod: Flow<Int> = currentTimePeriodChannel
            .map { it.displayString }
            .share()

        override val selectedDateTab: Flow<Int> = currentTimePeriodChannel
            .map { CoinHistoryTimePeriod.getIndexWithTimePeriod(it) }
            .share()

        override val valueChange24Hour: Flow<ColorValueString> = combine(
            currentPricePerUnitChannel,
            currentStartPricePerUnitChannel
        ) { price: Double, startPrice: Double ->
            val valueChangeDouble = price - startPrice
            ColorValueString.create(valueChangeDouble, currencyFormatter)
        }
            .distinctUntilChanged()
            .share()

        override val percentChange24Hour: Flow<ColorValueString> = combine(
            currentPricePerUnitChannel,
            currentStartPricePerUnitChannel
        ) { price, startPrice ->
            val percentChange = if (startPrice > 0.0) {
                ((price - startPrice) / startPrice)
            } else {
                0.0
            }
            ColorValueString.create(percentChange, formatterFactory.percentFormatter())
        }
            .distinctUntilChanged()
            .share()

        override val walletUnitsOwned: Flow<String> = walletUnitsOwnedChannel
            .map { formatterFactory.decimalFormatter().format(it) }
            .share()

        override val walletTotalValue: Flow<String> = walletTotalValueChannel
            .map { currencyFormatter.format(it) }
            .share()

        override val walletPriceChange24Hour: Flow<ColorValueString> =
            walletPriceChange24HourChannel
                .share()

        override val graphState: Flow<CoinDetailGraphState> = currentTimePeriodChannel
            .flatMapLatest { timePeriod -> interactor.getCoinHistory(coinSymbol, timePeriod) }
            .onEach { graphState ->
                if (graphState is CoinDetailGraphState.Success) {
                    currentStartPricePerUnitChannel.value = graphState.startingPrice
                }
            }
            .onStart { emit(CoinDetailGraphState.Loading) }
            .catch {
                Timber.e(it, "Get Coin History")
                emit(CoinDetailGraphState.Error)
            }
            .share()

        override val showAddCoinDialog: Flow<String> = showAddCoinDialogChannel

        override val showEditCoinAmountDialog: Flow<String> = showEditQuantityDialogChannel

        override val showConfirmRemoveDialog: Flow<String> = showConfirmRemoveCoinDialogChannel

        override val showNetworkError: Flow<Unit> = showNetworkErrorChannel

        //endregion

        //region Repository Calls

        private fun saveCoinToPortfolio(symbol: String, amount: Double) {
            viewModelScope.launch(Dispatchers.IO) {
                interactor.saveCoinToPortfolio(symbol, amount)
            }
        }

        private fun removeCoinFromPortfolio(symbol: String) {
            viewModelScope.launch(Dispatchers.IO) {
                interactor.removeCoinFromPortfolio(symbol)
            }
        }

        //endregion

        companion object {
            const val KEY_GRAPH_DATE_SELECTION = "KeyGraphDateSelection"
        }

    }
}
