package com.charliechristensen.coindetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.common.extensions.LiveDataExtensions.combineLatest
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
        val toolbarImageData: LiveData<ImageAndNamePair>
        val currentCoinPrice: LiveData<String>
        val low24Hour: LiveData<String>
        val high24Hour: LiveData<String>
        val isCoinInPortfolio: LiveData<Boolean>
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
    }

    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val formatterFactory: FormatterFactory,
        private val interactor: CoinDetailInteractor,
        @Assisted private val coinSymbol: String
    ) : BaseViewModel(), Inputs, Outputs {

        private val coinIsInPortfolioChannel = MutableLiveData<Boolean>()
        private val currentPricePerUnitChannel = MutableLiveData(0.0)
        private val pricePerUnit24HourLowChannel = MutableLiveData(0.0)
        private val pricePerUnit24HourHighChannel = MutableLiveData(0.0)
        private val walletUnitsOwnedChannel = MutableLiveData(0.0)
        private val walletTotalValueChannel = MutableLiveData(0.0)
        private val walletPriceChange24HourChannel = MutableLiveData(
            ColorValueString.create(0.0, formatterFactory.currencyFormatter())
        )
        private val toolbarImageDataChannel = MutableLiveData(ImageAndNamePair())
        private val currentStartPricePerUnitChannel = MutableLiveData(0.0)
        private val currentTimePeriodChannel: MutableLiveData<CoinHistoryTimePeriod> =
            MutableLiveData(CoinHistoryTimePeriod.OneDay)
        private val graphStateChannel: MutableLiveData<CoinDetailGraphState> =
            MutableLiveData(CoinDetailGraphState.Loading)

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
                .catch { showNetworkErrorChannel.setValue(Unit) }
                .launchIn(viewModelScope)

            currentTimePeriodChannel.asFlow()
                .onEach { graphStateChannel.setValue(CoinDetailGraphState.Loading) }
                .mapLatest { interactor.getCoinGraphData(coinSymbol, it) }
                .onEach { graphStateChannel.setValue(it) }
                .filterIsInstance<CoinDetailGraphState.Success>()
                .map { it.startingPrice }
                .onEach { currentStartPricePerUnitChannel.setValue(it) }
                .catch { graphStateChannel.setValue(CoinDetailGraphState.Error) }
                .launchIn(viewModelScope)

            interactor.addTemporarySubscription(coinSymbol, Constants.MyCurrency)
        }

        override fun onCleared() {
            super.onCleared()
            interactor.clearTemporarySubscriptions(Constants.MyCurrency)
        }

        //region Inputs

        override fun graphDateSelectionChanged(index: Int) {
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

        override val toolbarImageData: LiveData<ImageAndNamePair> = toolbarImageDataChannel.distinctUntilChanged()

        override val currentCoinPrice: LiveData<String> = currentPricePerUnitChannel
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override val low24Hour: LiveData<String> = pricePerUnit24HourLowChannel
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override val high24Hour: LiveData<String> = pricePerUnit24HourHighChannel
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override val isCoinInPortfolio: LiveData<Boolean> = coinIsInPortfolioChannel
                .distinctUntilChanged()

        override val percentChangeTimePeriod: LiveData<Int> = currentTimePeriodChannel
                .distinctUntilChanged()
                .map { it.displayString }

        override val valueChange24Hour: LiveData<ColorValueString> = combineLatest(
                currentPricePerUnitChannel,
                currentStartPricePerUnitChannel
            ) { price: Double, startPrice: Double ->
                val valueChangeDouble = price - startPrice
                ColorValueString.create(valueChangeDouble, formatterFactory.currencyFormatter())
            }.distinctUntilChanged()

        override val percentChange24Hour: LiveData<ColorValueString> = combineLatest(
                currentPricePerUnitChannel,
                currentStartPricePerUnitChannel
            ) { price, startPrice ->
                val percentChange = if (startPrice > 0.0) {
                    ((price - startPrice) / startPrice)
                } else {
                    0.0
                }
                ColorValueString.create(percentChange, formatterFactory.percentFormatter())
            }.distinctUntilChanged()

        override val walletUnitsOwned: LiveData<String> = walletUnitsOwnedChannel
                .distinctUntilChanged()
                .map { formatterFactory.decimalFormatter().format(it) }

        override val walletTotalValue: LiveData<String> = walletTotalValueChannel
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override val walletPriceChange24Hour: LiveData<ColorValueString> = walletPriceChange24HourChannel.distinctUntilChanged()

        override val graphState: LiveData<CoinDetailGraphState> = graphStateChannel.distinctUntilChanged()

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

        @AssistedInject.Factory
        interface Factory {
            fun create(coinSymbol: String): ViewModel
        }
    }
}
