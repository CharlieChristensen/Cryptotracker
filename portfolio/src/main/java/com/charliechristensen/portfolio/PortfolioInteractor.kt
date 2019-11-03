package com.charliechristensen.portfolio

import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class PortfolioInteractor @Inject constructor(
    private val repository: Repository,
    private val formatterFactory: FormatterFactory
) {

    fun listData(): Flow<PortfolioListData> =
        repository.getPortfolioData()
            .map { dbList -> mapPortfolioListData(dbList, formatterFactory) }

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
        val percentChange24Hour = if(portfolioOpenDouble > 0.0) {
            val percentChange = (portfolioValueDouble - portfolioOpenDouble) / portfolioOpenDouble
            ColorValueString.create(percentChange, formatterFactory.percentFormatter())
        } else {
            ColorValueString.create()
        }

        val portfolioValueChange = portfolioValueDouble - portfolioOpenDouble
        return PortfolioListData(
            coinList,
            percentChange24Hour,
            ColorValueString.create(portfolioValueChange, formatterFactory.currencyFormatter()),
            formatterFactory.currencyFormatter().format(portfolioValueDouble)
        )
    }

}

