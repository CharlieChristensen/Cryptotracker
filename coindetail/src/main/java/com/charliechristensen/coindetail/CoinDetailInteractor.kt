package com.charliechristensen.coindetail

import com.charliechristensen.coindetail.data.CoinDetailGraphState
import com.charliechristensen.coindetail.data.CoinHistoryGraphEntry
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ImageAndNamePair
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CoinDetailInteractor @Inject constructor(
    private val repository: Repository,
    private val formatterFactory: FormatterFactory
) {

    fun getCoinData(coinSymbol: String): Flow<CoinDetailData> =
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
                return@combine CoinDetailData(
                    coinIsInPortfolio = coinIsInPortfolio,
                    walletUnitsOwned = unitsOwned,
                    toolbarImageData = ImageAndNamePair(coinName, imageUrl)
                )
            }
            val walletTotalValueDouble = priceData.price * unitsOwned
            val walletTotalValueOpenDouble = priceData.open24Hour * unitsOwned
            val walletTotalValueChangeDouble = walletTotalValueDouble - walletTotalValueOpenDouble
            val walletPriceChange24Hour = ColorValueString.create(
                walletTotalValueChangeDouble,
                formatterFactory.currencyFormatter()
            )
            val imageNamePair = ImageAndNamePair(coinName, imageUrl)
            return@combine CoinDetailData(
                coinIsInPortfolio = coinIsInPortfolio,
                walletUnitsOwned = unitsOwned,
                pricePerUnit = priceData.price,
                pricePerUnit24HourLow = priceData.low24Hour,
                pricePerUnit24HourHigh = priceData.high24Hour,
                walletTotalValue = walletTotalValueDouble,
                walletPriceChange24Hour = walletPriceChange24Hour,
                toolbarImageData = imageNamePair
            )
        }.flowOn(Dispatchers.IO)

    suspend fun getCoinGraphData(
        coinSymbol: String,
        timePeriod: CoinHistoryTimePeriod
    ): CoinDetailGraphState = withContext(Dispatchers.IO) {
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
        return@withContext if (list.isEmpty()) {
            CoinDetailGraphState.NoData
        } else {
            CoinDetailGraphState.Success(list, color, startPrice)
        }
    }

    suspend fun saveCoinToPortfolio(symbol: String, amount: Double) {
        repository.addPortfolioCoin(symbol, amount)
    }

    suspend fun removeCoinFromPortfolio(symbol: String) {
        repository.removeCoinFromPortfolio(symbol)
    }

    fun addTemporarySubscription(symbol: String, currency: String) {
        repository.addTemporarySubscription(symbol, currency)
    }

    fun clearTemporarySubscriptions(currency: String) {
        repository.clearTemporarySubscriptions(currency)
    }

}
