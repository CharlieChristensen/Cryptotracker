package com.charliechristensen.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.common.call
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.cryptotracker.NavigationGraphDirections
import com.charliechristensen.portfolio.list.PortfolioListItem
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(item: PortfolioListItem)
    }

    interface Outputs {
        val portfolioState: LiveData<PortfolioListData>
        val showNetworkError: LiveData<Unit>
    }

    @ExperimentalCoroutinesApi
    class ViewModel @Inject constructor(
        private val navigator: Navigator,
        portfolioInteractor: PortfolioInteractor
    ) : BaseViewModel(), Inputs, Outputs {

        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun onClickItem(item: PortfolioListItem) {
            when (item) {
                is PortfolioListItem.Coin -> navigator.navigate(
                    NavigationGraphDirections.actionToCoinDetail(
                        item.symbol
                    )
                )
                PortfolioListItem.AddCoin -> navigator.navigate(
                    NavigationGraphDirections.actionToCoinList(
                        true
                    )
                )
            }
        }

        //endregion

        //region Outputs

        override val portfolioState: LiveData<PortfolioListData> =
            portfolioInteractor.listData()
                .flowOn(Dispatchers.IO)
                .catch { showNetworkErrorChannel.call() }
                .asLiveData()

        override val showNetworkError: LiveData<Unit> = showNetworkErrorChannel

        //endregion
    }
}
