package com.charliechristensen.coinlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

/**
 * View Model for searching all coins
 */
interface SearchCoinsViewModel {

    interface Inputs {
        fun onClickListItem(index: Int)
        fun setSearchQuery(query: CharSequence)
    }

    interface Outputs {
        fun coinList(): Flow<List<SearchCoinsListItem>>
        fun showCoinDetailController(): Flow<String>
        fun showNetworkError(): Flow<Unit>
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @AssistedInject constructor(
        private val repository: Repository,
        @Assisted private val filterOutOwnedCoins: Boolean,
        @Assisted private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

        private val showCoinDetailControllerChannel = BroadcastChannel<String>(1)
        private val showNetworkErrorChannel = BroadcastChannel<Unit>(1)
        private val coinListChannel = ConflatedBroadcastChannel<List<SearchCoinsListItem>>()
        private val searchQueryChannel = ConflatedBroadcastChannel<CharSequence>(savedState.get(
            KEY_SEARCH_QUERY_SAVED_STATE
        ) ?: "")

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            searchQueryChannel.asFlow()
                .debounce(400)
                .distinctUntilChanged()
                .onEach { savedState.set(KEY_SEARCH_QUERY_SAVED_STATE, it) }
                .flatMapLatest { searchCoinsWithQuery(it) }
                .map { coinList ->
                    coinList
                        .map { coin ->
                            SearchCoinsListItem.Coin(
                                coin.coinName,
                                coin.symbol,
                                coin.imageUrl ?: ""
                            )
                        }
                        .plus(SearchCoinsListItem.RefreshFooter)
                }
                .onEach(coinListChannel::send)
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
        }

        private fun refreshCoins() {
            viewModelScope.launch { forceRefreshCoinList() }
        }

        private suspend fun forceRefreshCoinList() = withContext(Dispatchers.IO) {
            try {
                repository.forceRefreshCoinListAndSaveToDb()
            } catch(exception: Exception) {
                coinListChannel.send(listOf(SearchCoinsListItem.RefreshFooter))
                showNetworkErrorChannel.send(Unit)
            }
        }

        private fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
            if (filterOutOwnedCoins) {
                repository.searchUnownedCoinWithQuery(query)
            } else {
                repository.searchCoinsWithQuery(query)
            }.map { coins ->
                coins.map { coin -> coin.toUi() }
            }

        //region Inputs

        override fun onClickListItem(index: Int) {
            when (val coin = coinListChannel.valueOrNull?.getOrNull(index)) {
                is SearchCoinsListItem.Coin -> showCoinDetailControllerChannel.offer(coin.symbol)
                is SearchCoinsListItem.RefreshFooter -> refreshCoins()
            }
        }

        override fun setSearchQuery(query: CharSequence) {
            searchQueryChannel.offer(query)
        }

        //endregion

        //region Outputs

        override fun coinList(): Flow<List<SearchCoinsListItem>> =
            coinListChannel.asFlow()
                .distinctUntilChanged()

        override fun showCoinDetailController(): Flow<String> =
            showCoinDetailControllerChannel.asFlow()

        override fun showNetworkError(): Flow<Unit> =
            showNetworkErrorChannel.asFlow()

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
