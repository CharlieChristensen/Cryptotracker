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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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

    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val interactor: SearchCoinsInteractor,
        private val navigator: Navigator,
        private val searchDataSourceFactory: SearchDataSourceFactory,
        @Assisted private val filterOutOwnedCoins: Boolean,
        @Assisted private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

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

        @FlowPreview
        override val coinList: LiveData<PagedList<SearchCoinsListItem>> =
            searchDataSourceFactory.toLiveData(pageSize = 50)

        override val showNetworkError: LiveData<Unit> = showNetworkErrorChannel

        //endregion

        @AssistedInject.Factory
        interface Factory {
            fun create(filterOutOwnedCoins: Boolean, savedState: SavedStateHandle): ViewModel
        }

        companion object {
            const val KEY_SEARCH_QUERY_SAVED_STATE = "KeySearchQuerySavedState"
        }
    }
}
