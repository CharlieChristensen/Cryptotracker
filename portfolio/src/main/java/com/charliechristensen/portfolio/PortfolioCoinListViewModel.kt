package com.charliechristensen.portfolio

import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.cryptotracker.NavigationGraphDirections
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(item: PortfolioListItem)
    }

    interface Outputs {
        val showNetworkError: Flow<Unit>
        val walletTotalValue: Flow<String>
        val percentChange24Hour: Flow<ColorValueString>
        val portfolioValueChange: Flow<ColorValueString>
        val coinList: Flow<List<PortfolioListItem>>
    }

    class ViewModel constructor(
        private val navigator: Navigator,
        portfolioInteractor: PortfolioInteractor
    ) : BaseViewModel(), Inputs, Outputs {

        private val showNetworkErrorEvent = MutableSharedFlow<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val portfolioStates: Flow<PortfolioListData> =
            portfolioInteractor.listData()
                .flowOn(Dispatchers.IO)
                .catch { showNetworkErrorEvent.emit(Unit) }

        //region Inputs

        override fun onClickItem(item: PortfolioListItem) {
            when (item) {
                is PortfolioListItem.Coin -> navigator.navigate(
                    NavigationGraphDirections.actionToCoinDetail(item.symbol)
                )
                PortfolioListItem.AddCoin -> navigator.navigate(
                    NavigationGraphDirections.actionToCoinList(true)
                )
            }
        }

        //endregion

        //region Outputs

        override val walletTotalValue: Flow<String> = portfolioStates
            .map { it.formattedValue }
            .share()

        override val percentChange24Hour: Flow<ColorValueString> = portfolioStates
            .map { it.percentChange24Hour }
            .share()

        override val portfolioValueChange: Flow<ColorValueString> = portfolioStates
            .map { it.portfolioValueChange }
            .share()

        override val coinList: Flow<List<PortfolioListItem>> = portfolioStates
            .map { it.coinList }
            .share()

        override val showNetworkError: Flow<Unit> = showNetworkErrorEvent

        //endregion
    }
}
