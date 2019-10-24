package com.charliechristensen.coindetail

import androidx.lifecycle.viewModelScope
import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.coindetail.data.CoinHistoryGraphEntry
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor
import com.charliechristensen.cryptotracker.data.websocket.WebSocketService
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
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
        fun showAddCoinDialog(): Flow<String>
        fun showEditCoinAmountDialog(): Flow<String>
        fun showConfirmRemoveDialog(): Flow<String>
        fun showNetworkError(): Flow<Unit>
    }

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

    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val repository: Repository,
        private val webSocket: WebSocketService,
        private val formatterFactory: FormatterFactory,
        @Assisted private val coinSymbol: String
    ) : BaseViewModel(), Inputs, Outputs {

        private val coinIsInPortfolioChannel = ConflatedBroadcastChannel<Boolean>()
        private val currentPricePerUnitChannel = ConflatedBroadcastChannel(0.0)
        private val pricePerUnit24HourLowChannel = ConflatedBroadcastChannel(0.0)
        private val pricePerUnit24HourHighChannel = ConflatedBroadcastChannel(0.0)
        private val walletUnitsOwnedChannel = ConflatedBroadcastChannel(0.0)
        private val walletTotalValueChannel = ConflatedBroadcastChannel(0.0)
        private val walletPriceChange24HourChannel = ConflatedBroadcastChannel(
            ColorValueString.create(
                0.0,
                formatterFactory.currencyFormatter()
            )
        )
        private val toolbarImageDataChannel = ConflatedBroadcastChannel(ImageAndNamePair())
        private val currentStartPricePerUnitChannel = ConflatedBroadcastChannel(0.0)
        private val currentTimePeriodChannel: ConflatedBroadcastChannel<CoinHistoryTimePeriod> =
            ConflatedBroadcastChannel(CoinHistoryTimePeriod.OneDay)
        private val graphStateChannel: ConflatedBroadcastChannel<CoinDetailGraphState> =
            ConflatedBroadcastChannel(CoinDetailGraphState.Loading)

        private val showAddCoinDialogChannel = BroadcastChannel<String>(1)
        private val showEditQuantityDialogChannel = BroadcastChannel<String>(1)
        private val showConfirmRemoveCoinDialogChannel = BroadcastChannel<String>(1)
        private val showNetworkErrorChannel = BroadcastChannel<Unit>(1)

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            getCoinData(coinSymbol)
                .onEach { priceData ->
                    coinIsInPortfolioChannel.send(priceData.coinIsInPortfolio)
                    currentPricePerUnitChannel.send(priceData.pricePerUnit)
                    pricePerUnit24HourLowChannel.send(priceData.pricePerUnit24HourLow)
                    pricePerUnit24HourHighChannel.send(priceData.pricePerUnit24HourHigh)
                    walletUnitsOwnedChannel.send(priceData.walletUnitsOwned)
                    walletTotalValueChannel.send(priceData.walletTotalValue)
                    walletPriceChange24HourChannel.send(
                        ColorValueString.create(
                            priceData.walletPriceChange24Hour,
                            formatterFactory.currencyFormatter()
                        )
                    )
                    toolbarImageDataChannel.send(
                        ImageAndNamePair(
                            priceData.coinName,
                            priceData.imageUrl
                        )
                    )
                }
                .catch { showNetworkErrorChannel.send(Unit) }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)

            webSocket.addTemporarySubscription(coinSymbol, Constants.MyCurrency)

            currentTimePeriodChannel.asFlow()
                .onEach { graphStateChannel.send(CoinDetailGraphState.Loading) }
                .mapLatest { getCoinGraphDatas(coinSymbol, it, repository) }
                .onEach(graphStateChannel::send)
                .filterIsInstance<CoinDetailGraphState.Success>()
                .map { it.startingPrice }
                .onEach(currentStartPricePerUnitChannel::send)
                .catch { graphStateChannel.send(CoinDetailGraphState.Error) }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }

        override fun onCleared() {
            super.onCleared()
            webSocket.clearTemporarySubscriptions(Constants.MyCurrency)
        }

        //region Inputs

        override fun graphDateSelectionChanged(index: Int) {
            currentTimePeriodChannel.offer(CoinHistoryTimePeriod.getTimePeriodWithIndex(index))
        }

        override fun addCoinButtonClicked() {
            showAddCoinDialogChannel.offer(coinSymbol)
        }

        override fun editQuantityButtonClicked() {
            showEditQuantityDialogChannel.offer(coinSymbol)
        }

        override fun removeFromPortfolioButtonClicked() {
            showConfirmRemoveCoinDialogChannel.offer(coinSymbol)
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

        override fun toolbarImageData(): Flow<ImageAndNamePair> =
            toolbarImageDataChannel.asFlow().distinctUntilChanged()

        override fun currentCoinPrice(): Flow<String> =
            currentPricePerUnitChannel.asFlow().distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun low24Hour(): Flow<String> =
            pricePerUnit24HourLowChannel.asFlow().distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun high24Hour(): Flow<String> =
            pricePerUnit24HourHighChannel.asFlow().distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun isCoinInPortfolio(): Flow<Boolean> =
            coinIsInPortfolioChannel.asFlow().distinctUntilChanged()

        override fun percentChangeTimePeriod(): Flow<Int> =
            currentTimePeriodChannel.asFlow().distinctUntilChanged()
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
            walletUnitsOwnedChannel.asFlow().distinctUntilChanged()
                .map { formatterFactory.decimalFormatter().format(it) }

        override fun walletTotalValue(): Flow<String> =
            walletTotalValueChannel.asFlow().distinctUntilChanged()
                .map { formatterFactory.currencyFormatter().format(it) }

        override fun walletPriceChange24Hour(): Flow<ColorValueString> =
            walletPriceChange24HourChannel.asFlow().distinctUntilChanged()

        override fun graphState(): Flow<CoinDetailGraphState> =
            graphStateChannel.asFlow().distinctUntilChanged()

        override fun showAddCoinDialog(): Flow<String> =
            showAddCoinDialogChannel.asFlow()

        override fun showEditCoinAmountDialog(): Flow<String> =
            showEditQuantityDialogChannel.asFlow()

        override fun showConfirmRemoveDialog(): Flow<String> =
            showConfirmRemoveCoinDialogChannel.asFlow()

        override fun showNetworkError(): Flow<Unit> =
            showNetworkErrorChannel.asFlow()

        //endregion

        //region Repository Calls

        private fun getCoinData(coinSymbol: String): Flow<PriceDataCombined> =
            combine(
                repository.getCoinPriceData(coinSymbol),
                repository.getUnitsOwnedForSymbol(coinSymbol),
                repository.getCoinDetails(coinSymbol)
            ) { priceList, unitsOwnedList, coinList ->
                val coinName = coinList.getOrNull(0)?.coinName ?: ""
                val imageUrl = coinList.getOrNull(0)?.imageUrl ?: ""
                val unitsOwned = unitsOwnedList.getOrElse(0) { 0.0 }
                val coinIsInPortfolio = unitsOwnedList.isNotEmpty()
                val priceData = priceList.getOrElse(0) {
                    return@combine PriceDataCombined(
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
                return@combine PriceDataCombined(
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

        private suspend fun getCoinGraphDatas(
            coinSymbol: String,
            timePeriod: CoinHistoryTimePeriod,
            repository: Repository
        ): CoinDetailGraphState {
            val coinHistory = repository.getHistoricalDataForCoin(coinSymbol, timePeriod)
            var color = ValueChangeColor.GREEN
            var validStartPrice = false
            var startPrice = 0.0
            val endPrice = coinHistory.historyElements.lastOrNull()?.close ?: 0.0
            val list = coinHistory.historyElements.map { coinHistoryElement: CoinHistoryElement ->
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
            return if (list.isEmpty()) {
                CoinDetailGraphState.NoData
            } else {
                CoinDetailGraphState.Success(list, color, startPrice)
            }
        }


        private fun saveCoinToPortfolio(symbol: String, amount: Double, repository: Repository) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addPortfolioCoin(symbol, amount)
            }
        }

        private fun removeCoinFromPortfolio(symbol: String, repository: Repository) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.removeCoinFromPortfolio(symbol)
            }
        }

        //endregion

        @AssistedInject.Factory
        interface Factory {
            fun create(coinSymbol: String): ViewModel
        }

    }
}
