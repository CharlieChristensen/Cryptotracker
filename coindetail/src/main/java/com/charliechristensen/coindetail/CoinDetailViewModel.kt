package com.charliechristensen.coindetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
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
        fun viewState(): Flow<CoinDetailData>
        fun toolbarImageData(): Flow<ImageAndNamePair>
        fun currentCoinPrice(): Flow<String>
        fun low24Hour(): Flow<String>
        fun high24Hour(): Flow<String>
        fun isCoinInPortfolio(): Flow<Boolean>
        fun percentChangeTimePeriod(): Flow<Int>
        fun valueChange24Hour(): Flow<ColorValueString>
        fun percentChange24Hour(): Flow<ColorValueString>
        fun walletUnitsOwned(): Flow<String>
        fun walletTotalValue(): Flow<String>
        fun walletPriceChange24Hour(): Flow<ColorValueString>
        fun graphState(): Flow<CoinDetailGraphState>
        fun showAddCoinDialog(): LiveData<String>
        fun showEditCoinAmountDialog(): LiveData<String>
        fun showConfirmRemoveDialog(): LiveData<String>
        fun showNetworkError(): LiveData<Unit>
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val formatterFactory: FormatterFactory,
        private val interactor: CoinDetailInteractor,
        @Assisted private val coinSymbol: String
    ) : BaseViewModel(), Inputs, Outputs {

        private val coinDetailViewState = ConflatedBroadcastChannel(
            CoinDetailData()
        )
        private val coinIsInPortfolioChannel = ConflatedBroadcastChannel<Boolean>()
        private val currentPricePerUnitChannel = ConflatedBroadcastChannel(0.0)
        private val pricePerUnit24HourLowChannel = ConflatedBroadcastChannel(0.0)
        private val pricePerUnit24HourHighChannel = ConflatedBroadcastChannel(0.0)
        private val walletUnitsOwnedChannel = ConflatedBroadcastChannel(0.0)
        private val walletTotalValueChannel = ConflatedBroadcastChannel(0.0)
        private val walletPriceChange24HourChannel = ConflatedBroadcastChannel(
            ColorValueString.create(0.0, formatterFactory.currencyFormatter())
        )
        private val toolbarImageDataChannel = ConflatedBroadcastChannel(ImageAndNamePair())
        private val currentStartPricePerUnitChannel = ConflatedBroadcastChannel(0.0)
        private val currentTimePeriodChannel: ConflatedBroadcastChannel<CoinHistoryTimePeriod> =
            ConflatedBroadcastChannel(CoinHistoryTimePeriod.OneDay)
        private val graphStateChannel: ConflatedBroadcastChannel<CoinDetailGraphState> =
            ConflatedBroadcastChannel(CoinDetailGraphState.Loading)

        private val showAddCoinDialogChannel = SingleLiveEvent<String>()
        private val showEditQuantityDialogChannel = SingleLiveEvent<String>()
        private val showConfirmRemoveCoinDialogChannel = SingleLiveEvent<String>()
        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            interactor.getCoinData(coinSymbol)
                .onEach { priceData ->
                    coinIsInPortfolioChannel.send(priceData.coinIsInPortfolio)
                    currentPricePerUnitChannel.send(priceData.pricePerUnit)
                    pricePerUnit24HourLowChannel.send(priceData.pricePerUnit24HourLow)
                    pricePerUnit24HourHighChannel.send(priceData.pricePerUnit24HourHigh)
                    walletUnitsOwnedChannel.send(priceData.walletUnitsOwned)
                    walletTotalValueChannel.send(priceData.walletTotalValue)
                    walletPriceChange24HourChannel.send(priceData.walletPriceChange24Hour)
                    toolbarImageDataChannel.send(priceData.toolbarImageData)
                }
                .flowOn(Dispatchers.IO)
                .catch { showNetworkErrorChannel.setValue(Unit) }
                .launchIn(viewModelScope)

            currentTimePeriodChannel.asFlow()
                .onEach { graphStateChannel.send(CoinDetailGraphState.Loading) }
                .mapLatest { interactor.getCoinGraphData(coinSymbol, it) }
                .onEach(graphStateChannel::send)
                .filterIsInstance<CoinDetailGraphState.Success>()
                .map { it.startingPrice }
                .onEach(currentStartPricePerUnitChannel::send)
                .flowOn(Dispatchers.IO)
                .catch { graphStateChannel.send(CoinDetailGraphState.Error) }
                .launchIn(viewModelScope)

            interactor.addTemporarySubscription(coinSymbol, Constants.MyCurrency)
        }

        override fun onCleared() {
            super.onCleared()
            interactor.clearTemporarySubscriptions(Constants.MyCurrency)
        }

        //region Inputs

        override fun graphDateSelectionChanged(index: Int) {
            currentTimePeriodChannel.offer(CoinHistoryTimePeriod.getTimePeriodWithIndex(index))
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

        override fun viewState(): Flow<CoinDetailData> =
            coinDetailViewState.asFlow()

        override fun toolbarImageData(): Flow<ImageAndNamePair> =
            toolbarImageDataChannel.asFlow()
                .distinctUntilChanged()

        override fun currentCoinPrice(): Flow<String> =
            currentPricePerUnitChannel.asFlow()
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun low24Hour(): Flow<String> =
            pricePerUnit24HourLowChannel.asFlow()
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun high24Hour(): Flow<String> =
            pricePerUnit24HourHighChannel.asFlow()
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun isCoinInPortfolio(): Flow<Boolean> =
            coinIsInPortfolioChannel.asFlow()
                .distinctUntilChanged()

        override fun percentChangeTimePeriod(): Flow<Int> =
            currentTimePeriodChannel.asFlow()
                .distinctUntilChanged()
                .map { it.displayString }

        override fun valueChange24Hour(): Flow<ColorValueString> = combine(
            currentPricePerUnitChannel.asFlow(),
            currentStartPricePerUnitChannel.asFlow()
        ) { price: Double, startPrice: Double ->
            val valueChangeDouble = price - startPrice
            ColorValueString.create(valueChangeDouble, formatterFactory.currencyFormatter())
        }.distinctUntilChanged()

        override fun percentChange24Hour(): Flow<ColorValueString> =
            combine(
                currentPricePerUnitChannel.asFlow(),
                currentStartPricePerUnitChannel.asFlow()
            ) { price, startPrice ->
                val percentChange = if (startPrice > 0.0) {
                    ((price - startPrice) / startPrice)
                } else {
                    0.0
                }
                ColorValueString.create(percentChange, formatterFactory.percentFormatter())
            }.distinctUntilChanged()


        override fun walletUnitsOwned(): Flow<String> =
            walletUnitsOwnedChannel.asFlow()
                .distinctUntilChanged()
                .map { formatterFactory.decimalFormatter().format(it) }

        override fun walletTotalValue(): Flow<String> =
            walletTotalValueChannel.asFlow()
                .distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun walletPriceChange24Hour(): Flow<ColorValueString> =
            walletPriceChange24HourChannel.asFlow()
                .distinctUntilChanged()

        override fun graphState(): Flow<CoinDetailGraphState> =
            graphStateChannel.asFlow()
                .distinctUntilChanged()

        override fun showAddCoinDialog(): LiveData<String> =
            showAddCoinDialogChannel

        override fun showEditCoinAmountDialog(): LiveData<String> =
            showEditQuantityDialogChannel

        override fun showConfirmRemoveDialog(): LiveData<String> =
            showConfirmRemoveCoinDialogChannel

        override fun showNetworkError(): LiveData<Unit> =
            showNetworkErrorChannel

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

