package com.charliechristensen.coindetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
        val currentCoinPrice: LiveData<String>
        val low24Hour: LiveData<String>
        val high24Hour: LiveData<String>
        val isCoinInPortfolio: Flow<Boolean>
        val percentChangeTimePeriod: LiveData<Int>
        val valueChange24Hour: LiveData<ColorValueString>
        val percentChange24Hour: LiveData<ColorValueString>
        val walletUnitsOwned: LiveData<String>
        val walletTotalValue: LiveData<String>
        val walletPriceChange24Hour: LiveData<ColorValueString>
        val graphState: LiveData<CoinDetailGraphState>
        val showAddCoinDialog: LiveData<String>
        val showEditCoinAmountDialog: LiveData<String>
        val showConfirmRemoveDialog: LiveData<String>
        val showNetworkError: LiveData<Unit>
        val selectedDateTab: LiveData<Int>
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
        private val showAddCoinDialogChannel = SingleLiveEvent<String>()
        private val showEditQuantityDialogChannel = SingleLiveEvent<String>()
        private val showConfirmRemoveCoinDialogChannel = SingleLiveEvent<String>()
        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()

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
                    showNetworkErrorChannel.setValue(Unit)
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
            showAddCoinDialogChannel.value = coinSymbol
        }

        override fun editQuantityButtonClicked() {
            showEditQuantityDialogChannel.value = coinSymbol
        }

        override fun removeFromPortfolioButtonClicked() {
            showConfirmRemoveCoinDialogChannel.value = coinSymbol
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

        override val currentCoinPrice: LiveData<String> = currentPricePerUnitChannel
            .map { currencyFormatter.format(it) }
            .asLiveData()

        override val low24Hour: LiveData<String> = pricePerUnit24HourLowChannel
            .map { currencyFormatter.format(it) }
            .asLiveData()

        override val high24Hour: LiveData<String> = pricePerUnit24HourHighChannel
            .map { currencyFormatter.format(it) }
            .asLiveData()

        override val isCoinInPortfolio: Flow<Boolean> = coinIsInPortfolioChannel

        override val percentChangeTimePeriod: LiveData<Int> = currentTimePeriodChannel
            .map { it.displayString }
            .asLiveData()

        override val selectedDateTab: LiveData<Int> = currentTimePeriodChannel
            .map { CoinHistoryTimePeriod.getIndexWithTimePeriod(it) }
            .asLiveData()

        override val valueChange24Hour: LiveData<ColorValueString> = combine(
            currentPricePerUnitChannel,
            currentStartPricePerUnitChannel
        ) { price: Double, startPrice: Double ->
            val valueChangeDouble = price - startPrice
            ColorValueString.create(valueChangeDouble, currencyFormatter)
        }
            .distinctUntilChanged()
            .asLiveData()

        override val percentChange24Hour: LiveData<ColorValueString> = combine(
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
            .asLiveData()

        override val walletUnitsOwned: LiveData<String> = walletUnitsOwnedChannel
            .map { formatterFactory.decimalFormatter().format(it) }
            .asLiveData()

        override val walletTotalValue: LiveData<String> = walletTotalValueChannel
            .map { currencyFormatter.format(it) }
            .asLiveData()

        override val walletPriceChange24Hour: LiveData<ColorValueString> =
            walletPriceChange24HourChannel.asLiveData()

        @ExperimentalCoroutinesApi
        override val graphState: LiveData<CoinDetailGraphState> = currentTimePeriodChannel
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
            .asLiveData()

        override val showAddCoinDialog: LiveData<String> = showAddCoinDialogChannel

        override val showEditCoinAmountDialog: LiveData<String> = showEditQuantityDialogChannel

        override val showConfirmRemoveDialog: LiveData<String> = showConfirmRemoveCoinDialogChannel

        override val showNetworkError: LiveData<Unit> = showNetworkErrorChannel

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
