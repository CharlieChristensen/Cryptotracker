package com.charliechristensen.coinlist

import androidx.lifecycle.*
import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.common.call
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

/**
 * View Model for searching all coins
 */
interface SearchCoinsViewModel {

    interface Inputs {
        fun setSearchQuery(query: CharSequence)
        fun onClickCoin(symbol: String)
        fun onClickRefresh()
    }

    interface Outputs {
        val coinList: LiveData<List<SearchCoinsListItem>>
        val showCoinDetailController: LiveData<String>
        val showNetworkError: LiveData<Unit>
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val interactor: SearchCoinsInteractor,
        @Assisted private val filterOutOwnedCoins: Boolean,
        @Assisted private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

        private val showCoinDetailControllerChannel = SingleLiveEvent<String>()
        private val showNetworkErrorChannel = SingleLiveEvent<Unit>()
        private val searchQueryChannel = ConflatedBroadcastChannel<CharSequence>(
            savedState.get(
                KEY_SEARCH_QUERY_SAVED_STATE
            ) ?: ""
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
            showCoinDetailControllerChannel.value = symbol
        }

        override fun onClickRefresh() {
            refreshCoins()
        }

        override fun setSearchQuery(query: CharSequence) {
            searchQueryChannel.offer(query)
        }

        //endregion

        //region Outputs

        override val coinList: LiveData<List<SearchCoinsListItem>> =
            flowOf(
                flowOf(searchQueryChannel.valueOrNull ?: ""),
                searchQueryChannel.asFlow()
                    .debounce(400)
            ).flattenMerge()
                .distinctUntilChanged()
                .onEach { savedState.set(KEY_SEARCH_QUERY_SAVED_STATE, it) }
                .flatMapLatest { interactor.searchCoinsWithQuery(it, filterOutOwnedCoins) }
                .catch { emit(listOf(SearchCoinsListItem.RefreshFooter)) }
                .flowOn(Dispatchers.IO)
                .asLiveData()

        override val showCoinDetailController: LiveData<String> = showCoinDetailControllerChannel

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
