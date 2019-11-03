package com.charliechristensen.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.common.call
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
interface PortfolioCoinListViewModel {

    interface Inputs {
        fun onClickItem(item: PortfolioListItem)
    }

    interface Outputs {
        val portfolioState: LiveData<PortfolioListData>
        val showCoinDetailController: LiveData<String>
        val showChooseCoinsListController: LiveData<Unit>
        val showNetworkError: LiveData<Unit>
    }

    class ViewModel @Inject constructor(
        portfolioInteractor: PortfolioInteractor
    ) : BaseViewModel(), Inputs, Outputs {

        private val showCoinDetailControllerChannel = SingleLiveEvent<String>()
        private val showAddCoinListControllerChannel = SingleLiveEvent<Unit>()
        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun onClickItem(item: PortfolioListItem) {
            when (item) {
                is PortfolioListItem.Coin -> showCoinDetailControllerChannel.setValue(item.symbol)
                PortfolioListItem.AddCoin -> showAddCoinListControllerChannel.call()
            }
        }

        //endregion

        //region Outputs

        override val portfolioState: LiveData<PortfolioListData> =
            portfolioInteractor.listData()
                .flowOn(Dispatchers.IO)
                .catch { showNetworkErrorChannel.call() }
                .asLiveData()

        override val showCoinDetailController: LiveData<String> = showCoinDetailControllerChannel

        override val showChooseCoinsListController: LiveData<Unit> =
            showAddCoinListControllerChannel

        override val showNetworkError: LiveData<Unit> = showNetworkErrorChannel

        //endregion

    }

}
