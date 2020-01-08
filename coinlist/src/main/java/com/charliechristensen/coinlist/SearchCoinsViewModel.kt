package com.charliechristensen.coinlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coinlist.list.SearchCoinsListItem
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SearchCoinsViewModel {

    interface Inputs {
        fun setSearchQuery(query: CharSequence)
        fun onClickCoin(symbol: String)
        fun onClickRefresh()
    }

    interface Outputs {
        val coinList: LiveData<List<SearchCoinsListItem>>
        val showNetworkError: LiveData<Unit>
    }

    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val interactor: SearchCoinsInteractor,
        private val navigator: Navigator,
        @Assisted private val filterOutOwnedCoins: Boolean,
        @Assisted private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()
        private val searchQueryLiveData = MutableLiveData<CharSequence>(
            savedState.get(KEY_SEARCH_QUERY_SAVED_STATE) ?: ""
        )

        val inputs: Inputs = this
        val outputs: Outputs = this

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
            searchQueryLiveData.value = query
        }

        //endregion

        //region Outputs

        @FlowPreview
        override val coinList: LiveData<List<SearchCoinsListItem>> =
            flowOf(
                flowOf(searchQueryLiveData.value ?: ""),
                searchQueryLiveData.asFlow().debounce(400)
            ).flattenMerge()
                .distinctUntilChanged()
                .onEach { savedState.set(KEY_SEARCH_QUERY_SAVED_STATE, it) }
                .flatMapLatest { interactor.searchCoinsWithQuery(it, filterOutOwnedCoins) }
                .catch { emit(listOf(SearchCoinsListItem.RefreshFooter)) }
                .flowOn(Dispatchers.IO)
                .asLiveData()

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
