package com.charliechristensen.cryptotracker.cryptotracker.portfolio

import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.cryptotracker.portfolio.list.PortfolioListItem
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Portfolio ViewModel
 */

interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(index: Int)
    }

    interface Outputs {
        fun getPortfolioValue(): Observable<String>
        fun getPortfolioValueChange(): Observable<ColorValueString>
        fun getPortfolioPercentChange24Hour(): Observable<ColorValueString>
        fun coinList(): Observable<List<PortfolioListItem>>
        fun showCoinDetailController(): Observable<String>
        fun showChooseCoinsListController(): Observable<Unit>
        fun showNetworkError(): Observable<Unit>
    }

    class ViewModel @Inject constructor(
        repository: Repository,
        private val formatterFactory: FormatterFactory
    ) : BaseViewModel(), Inputs, Outputs {

        private val portfolioValueRelay: BehaviorRelay<Double> = BehaviorRelay.createDefault(0.0)
        private val portfolioOpenRelay: BehaviorRelay<Double> = BehaviorRelay.createDefault(0.0)
        private val portfolioValueChangeRelay: BehaviorRelay<Double> =
            BehaviorRelay.createDefault(0.0)

        private val coinListRelay: BehaviorRelay<List<PortfolioListItem>> = BehaviorRelay.create()

        private val showCoinDetailControllerRelay: PublishRelay<String> = PublishRelay.create()
        private val showAddCoinListControllerRelay: PublishRelay<Unit> = PublishRelay.create()
        private val showNetworkErrorRelay: PublishRelay<Unit> = PublishRelay.create()


        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            getPortfolioDataForSymbols(repository, formatterFactory)
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onNext = {
                        portfolioOpenRelay.accept(it.portfolioOpen)
                        portfolioValueRelay.accept(it.portfolioValue)
                        portfolioValueChangeRelay.accept(it.portfolioValueChange)
                        coinListRelay.accept(it.coinList)
                    },
                    onError = { showNetworkErrorRelay.accept(Unit) }
                )
                .addTo(disposables)
        }

        //region Inputs

        override fun onClickItem(index: Int) {
            when (val item = coinListRelay.value?.getOrNull(index)) {
                is PortfolioListItem.Coin -> showCoinDetailControllerRelay.accept(item.symbol)
                PortfolioListItem.AddCoin -> showAddCoinListControllerRelay.accept(Unit)
            }
        }

        //endregion

        //region Outputs

        override fun getPortfolioValue(): Observable<String> = portfolioValueRelay
            .distinctUntilChanged()
            .map { formatterFactory.currencyFormatter().format(it) }
            .distinctUntilChanged()

        override fun getPortfolioValueChange(): Observable<ColorValueString> =
            portfolioValueChangeRelay
                .map { ColorValueString.create(it, formatterFactory.currencyFormatter()) }

        override fun getPortfolioPercentChange24Hour(): Observable<ColorValueString> =
            Observable.combineLatest(
                portfolioValueRelay,
                portfolioOpenRelay,
                BiFunction<Double, Double, ColorValueString> { currentValue, openValue ->
                    if (openValue > 0.0) {
                        val percentChange = (currentValue - openValue) / openValue
                        ColorValueString.create(percentChange, formatterFactory.percentFormatter())
                    } else {
                        ColorValueString.create()
                    }
                })
                .distinctUntilChanged()

        override fun coinList(): Observable<List<PortfolioListItem>> =
            coinListRelay.distinctUntilChanged()

        override fun showCoinDetailController(): Observable<String> =
            showCoinDetailControllerRelay

        override fun showChooseCoinsListController(): Observable<Unit> =
            showAddCoinListControllerRelay

        override fun showNetworkError(): Observable<Unit> =
            showNetworkErrorRelay

        //endregion

        //region Repository Calls

        private fun getPortfolioDataForSymbols(
            repository: Repository,
            formatterFactory: FormatterFactory
        ): Observable<PortfolioListData> = repository.getPortfolioData()
            .map { dbList -> mapPortfolioListData(dbList, formatterFactory) }

        //endregion

        private fun mapPortfolioListData(
            dbList: List<DbCoinWithPriceAndAmount>,
            formatterFactory: FormatterFactory
        ): PortfolioListData {
            var portfolioValueDouble = 0.0
            var portfolioOpenDouble = 0.0
            val coinList = dbList.map { coin ->
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
            }.plus(PortfolioListItem.AddCoin)
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