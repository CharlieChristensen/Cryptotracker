package com.charliechristensen.portfolio

import androidx.lifecycle.viewModelScope
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Portfolio ViewModel
 */

@ExperimentalCoroutinesApi
interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(index: Int)
    }

    interface Outputs {
        fun getPortfolioValue(): Flow<String>
        fun getPortfolioValueChange(): Flow<ColorValueString>
        fun getPortfolioPercentChange24Hour(): Flow<ColorValueString>
        fun coinList(): Flow<List<PortfolioListItem>>
        fun showCoinDetailController(): Flow<String>
        fun showChooseCoinsListController(): Flow<Unit>
        fun showNetworkError(): Flow<Unit>
    }


    @FlowPreview
    class ViewModel @Inject constructor(
        repository: Repository,
        private val formatterFactory: FormatterFactory
    ) : BaseViewModel(), Inputs, Outputs {

        private val portfolioValueChannel = ConflatedBroadcastChannel(0.0)
        private val portfolioOpenChannel = ConflatedBroadcastChannel(0.0)
        private val portfolioValueChangeChannel = ConflatedBroadcastChannel(0.0)
        private val coinListChannel = ConflatedBroadcastChannel<List<PortfolioListItem>>()
        private val showCoinDetailControllerChannel = BroadcastChannel<String>(1)
        private val showAddCoinListControllerChannel = BroadcastChannel<Unit>(1)
        private val showNetworkErrorChannel = BroadcastChannel<Unit>(1)

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            repository.getPortfolioData()
                .map { dbList -> mapPortfolioListData(dbList, formatterFactory) }
                .flowOn(Dispatchers.IO)
                .onEach { portfolioData ->
                    portfolioOpenChannel.send(portfolioData.portfolioOpen)
                    portfolioValueChannel.send(portfolioData.portfolioValue)
                    portfolioValueChangeChannel.send(portfolioData.portfolioValueChange)
                    coinListChannel.send(portfolioData.coinList)
                }
                .catch { showNetworkErrorChannel.send(Unit) }
                .launchIn(viewModelScope)
        }

        //region Inputs

        override fun onClickItem(index: Int) {
            when (val item = coinListChannel.value.getOrNull(index)) {
                is PortfolioListItem.Coin -> showCoinDetailControllerChannel.offer(item.symbol)
                PortfolioListItem.AddCoin -> showAddCoinListControllerChannel.offer(Unit)
            }
        }

        //endregion

        //region Outputs

        override fun getPortfolioValue(): Flow<String> = portfolioValueChannel.asFlow()
            .distinctUntilChanged()
            .map { formatterFactory.currencyFormatter().format(it) }
            .distinctUntilChanged()

        override fun getPortfolioValueChange(): Flow<ColorValueString> =
            portfolioValueChangeChannel.asFlow()
                .map { ColorValueString.create(it, formatterFactory.currencyFormatter()) }

        override fun getPortfolioPercentChange24Hour(): Flow<ColorValueString> = combine(
            portfolioValueChannel.asFlow(),
            portfolioOpenChannel.asFlow()
        ) { currentValue, openValue ->
            if (openValue > 0.0) {
                val percentChange = (currentValue - openValue) / openValue
                ColorValueString.create(percentChange, formatterFactory.percentFormatter())
            } else {
                ColorValueString.create()
            }
        }.distinctUntilChanged()

        override fun coinList(): Flow<List<PortfolioListItem>> = coinListChannel.asFlow()

        override fun showCoinDetailController(): Flow<String> =
            showCoinDetailControllerChannel.asFlow()

        override fun showChooseCoinsListController(): Flow<Unit> =
            showAddCoinListControllerChannel.asFlow()

        override fun showNetworkError(): Flow<Unit> =
            showNetworkErrorChannel.asFlow()

        //endregion

        private fun mapPortfolioListData(
            dbList: List<DbCoinWithPriceAndAmount>,
            formatterFactory: FormatterFactory
        ): PortfolioListData {
            var portfolioValueDouble = 0.0
            var portfolioOpenDouble = 0.0
            val coinList = dbList
                .map { coin ->
                    val priceChangePerUnitDouble = coin.price - coin.open24Hour
                    val walletTotalValueDouble = coin.price * coin.amountOwned
                    val walletTotalValueOpenDouble = coin.open24Hour * coin.amountOwned
                    val walletTotalValueChangeDouble =
                        walletTotalValueDouble - walletTotalValueOpenDouble

                    val dollarFormat = formatterFactory.currencyFormatter()
                    val walletTotalValueChange =
                        ColorValueString.create(walletTotalValueChangeDouble, dollarFormat)
                    val priceChangePerUnit =
                        ColorValueString.create(priceChangePerUnitDouble, dollarFormat)
                    val currentPrice = dollarFormat.format(coin.price)
                    val walletTotalValue = dollarFormat.format(walletTotalValueDouble)

                    portfolioValueDouble += walletTotalValueDouble
                    portfolioOpenDouble += walletTotalValueOpenDouble

                    PortfolioListItem.Coin(
                        coin.symbol,
                        coin.imageUrl,
                        currentPrice,
                        priceChangePerUnit,
                        walletTotalValueChange,
                        walletTotalValue
                    )
                }
                .plus(PortfolioListItem.AddCoin)
            val portfolioValueChange = portfolioValueDouble - portfolioOpenDouble
            return PortfolioListData(
                coinList,
                portfolioOpenDouble,
                portfolioValueDouble,
                portfolioValueChange
            )
        }

        private class PortfolioListData(
            val coinList: List<PortfolioListItem>,
            val portfolioOpen: Double,
            val portfolioValue: Double,
            val portfolioValueChange: Double
        )

    }


}
