package com.charliechristensen.coinlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.coinlist.list.SearchDataSource
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.cryptotracker.NavigationGraphDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SearchCoinsViewModel {

    interface Inputs {
        fun onClickCoin(symbol: String)
        fun onClickRefresh()
    }

    interface Outputs {
        fun coinList(searchTerm: String): Flow<PagingData<SearchCoinsListItem>>
        val showNetworkError: Flow<Unit>
    }

    class ViewModel constructor(
        private val interactor: SearchCoinsInteractor,
        private val navigator: Navigator,
        private val savedState: SavedStateHandle,
        private val filterOutOwnedCoins: Boolean
    ) : BaseViewModel(), Inputs, Outputs {

        private val showNetworkErrorEvent = MutableSharedFlow<Unit>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        private fun refreshCoins() {
            viewModelScope.launch { forceRefreshCoinList() }
        }

        private suspend fun forceRefreshCoinList() = withContext(Dispatchers.IO) {
            try {
                interactor.forceRefreshCoinListAndSaveToDb()
            } catch (exception: Exception) {
                showNetworkErrorEvent.emit(Unit)
            }
        }

        //region Inputs

        override fun onClickCoin(symbol: String) {
            navigator.navigate(NavigationGraphDirections.actionToCoinDetail(symbol))
        }

        override fun onClickRefresh() {
            refreshCoins()
        }

        //endregion

        //region Outputs

        override fun coinList(searchTerm: String): Flow<PagingData<SearchCoinsListItem>> {
            savedState.set(KEY_SEARCH_QUERY_SAVED_STATE, searchTerm)
            return Pager(
                config = PagingConfig(pageSize = 50),
                initialKey = 0
            ) {
                SearchDataSource(
                    interactor,
                    searchTerm,
                    filterOutOwnedCoins
                )
            }.flow.cachedIn(viewModelScope)
        }

        override val showNetworkError: Flow<Unit> = showNetworkErrorEvent

        //endregion

        companion object {
            const val KEY_SEARCH_QUERY_SAVED_STATE = "KeySearchQuerySavedState"
        }
    }
}
