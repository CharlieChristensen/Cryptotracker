package com.charliechristensen.portfolio

import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PortfolioInteractor constructor(
    private val repository: Repository,
    private val formatterFactory: FormatterFactory
) {

    fun listData(): Flow<PortfolioListData> =
        repository.getPortfolioData()
            .map { dbList -> mapPortfolioListData(dbList, formatterFactory) }

    private suspend fun mapPortfolioListData(
        dbList: List<CoinWithPriceAndAmount>,
        formatterFactory: FormatterFactory
    ): PortfolioListData {
        val currencyFormatter = formatterFactory.currencyFormatter(repository.getCurrency())
        var portfolioValueDouble = 0.0
        var portfolioOpenDouble = 0.0
        val coinList = dbList
            .map { coin ->
                val priceChangePerUnitDouble = coin.price - coin.open24Hour
                val walletTotalValueDouble = coin.price * coin.amountOwned
                val walletTotalValueOpenDouble = coin.open24Hour * coin.amountOwned
                val walletTotalValueChangeDouble =
                    walletTotalValueDouble - walletTotalValueOpenDouble

                val walletTotalValueChange =
                    ColorValueString.create(walletTotalValueChangeDouble, currencyFormatter)
                val priceChangePerUnit =
                    ColorValueString.create(priceChangePerUnitDouble, currencyFormatter)
                val currentPrice = currencyFormatter.format(coin.price)
                val walletTotalValue = currencyFormatter.format(walletTotalValueDouble)

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
        val percentChange24Hour = if (portfolioOpenDouble > 0.0) {
            val percentChange = (portfolioValueDouble - portfolioOpenDouble) / portfolioOpenDouble
            ColorValueString.create(percentChange, formatterFactory.percentFormatter())
        } else {
            ColorValueString.create()
        }

        val portfolioValueChange = portfolioValueDouble - portfolioOpenDouble
        return PortfolioListData(
            coinList,
            percentChange24Hour,
            ColorValueString.create(portfolioValueChange, currencyFormatter),
            currencyFormatter.format(portfolioValueDouble)
        )
    }
}
