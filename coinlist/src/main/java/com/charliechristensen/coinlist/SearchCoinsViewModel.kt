package com.charliechristensen.coinlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.coinlist.list.SearchDataSourceFactory
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.common.call
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.cryptotracker.NavigationGraphDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SearchCoinsViewModel {

    interface Inputs {
        fun setSearchQuery(query: CharSequence)
        fun onClickCoin(symbol: String)
        fun onClickRefresh()
    }

    interface Outputs {
        val coinList: LiveData<PagedList<SearchCoinsListItem>>
        val showNetworkError: LiveData<Unit>
    }

    class ViewModel constructor(
        private val interactor: SearchCoinsInteractor,
        private val navigator: Navigator,
        private val savedState: SavedStateHandle,
        filterOutOwnedCoins: Boolean
    ) : BaseViewModel(), Inputs, Outputs {

        private val searchDataSourceFactory = SearchDataSourceFactory(interactor, filterOutOwnedCoins)
        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            searchDataSourceFactory.setSearchTerm(savedState.get(KEY_SEARCH_QUERY_SAVED_STATE) ?: "")
        }

        private fun refreshCoins() {
            viewModelScope.launch { forceRefreshCoinList() }
        }

        private suspend fun forceRefreshCoinList() = withContext(Dispatchers.IO) {
            try {
                interactor.forceRefreshCoinListAndSaveToDb()
            } catch (exception: Exception) {
                withContext(Dispatchers.Main.immediate) { showNetworkErrorChannel.call() }
            }
        }

        //region Inputs

        override fun onClickCoin(symbol: String) {
            navigator.navigate(NavigationGraphDirections.actionToCoinDetail(symbol))
        }

        override fun onClickRefresh() {
            refreshCoins()
        }

        override fun setSearchQuery(query: CharSequence) {
            savedState.set(KEY_SEARCH_QUERY_SAVED_STATE, query)
            searchDataSourceFactory.setSearchTerm(query.toString())
        }

        //endregion

        //region Outputs

        override val coinList: LiveData<PagedList<SearchCoinsListItem>> =
            searchDataSourceFactory.toLiveData(pageSize = 50)

        override val showNetworkError: LiveData<Unit> = showNetworkErrorChannel

        //endregion

        companion object {
            const val KEY_SEARCH_QUERY_SAVED_STATE = "KeySearchQuerySavedState"
        }
    }
}
