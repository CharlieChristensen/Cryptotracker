package com.charliechristensen.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
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

interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(item: PortfolioListItem)
    }

    interface Outputs {
        val showNetworkError: Flow<Unit>
        val walletTotalValue: LiveData<String>
        val percentChange24Hour: LiveData<ColorValueString>
        val portfolioValueChange: LiveData<ColorValueString>
        val coinList: LiveData<List<PortfolioListItem>>
    }

    class ViewModel constructor(
        private val navigator: Navigator,
        portfolioInteractor: PortfolioInteractor
    ) : BaseViewModel(), Inputs, Outputs {

        private val showNetworkErrorLiveData = MutableSharedFlow<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val portfolioStates: Flow<PortfolioListData> =
            portfolioInteractor.listData()
                .flowOn(Dispatchers.IO)
                .catch { showNetworkErrorLiveData.emit(Unit) }

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

        override val walletTotalValue: LiveData<String> = portfolioStates
            .map { it.formattedValue }
            .asLiveData()

        override val percentChange24Hour: LiveData<ColorValueString> = portfolioStates
            .map { it.percentChange24Hour }
            .asLiveData()

        override val portfolioValueChange: LiveData<ColorValueString> = portfolioStates
            .map { it.portfolioValueChange }
            .asLiveData()

        override val coinList: LiveData<List<PortfolioListItem>> = portfolioStates
            .map { it.coinList }
            .asLiveData()

        override val showNetworkError: Flow<Unit> = showNetworkErrorLiveData

        //endregion
    }
}
