package com.charliechristensen.cryptotracker.cryptotracker.coinList

import androidx.lifecycle.SavedStateHandle
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.cryptotracker.coinList.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * View Model for searching all coins
 */
interface SearchCoinsViewModel {

    interface Inputs {
        fun onClickListItem(index: Int)
        fun setSearchQuery(query: CharSequence)
    }

    interface Outputs {
        fun coinList(): Observable<List<SearchCoinsListItem>>
        fun showCoinDetailController(): Observable<String>
        fun showNetworkError(): Observable<Unit>
    }

    class ViewModel @AssistedInject constructor(
        private val repository: Repository,
        @Assisted private val filterOutOwnedCoins: Boolean,
        @Assisted private val savedState: SavedStateHandle
    ) : BaseViewModel(), Inputs, Outputs {

        private val showCoinDetailControllerRelay = PublishRelay.create<String>()
        private val showNetworkErrorRelay = PublishRelay.create<Unit>()
        private val coinListRelay = BehaviorRelay.create<List<SearchCoinsListItem>>()
        private val searchQueryRelay =
            BehaviorRelay.createDefault<CharSequence>(savedState.get("searchQuery") ?: "")

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            searchQueryRelay.debounce(400, TimeUnit.MILLISECONDS)
                .mergeWith(searchQueryRelay.first("")) //Allows to emit saved state without waiting for debounce
                .distinctUntilChanged()
                .doOnNext { savedState.set("searchQuery", it) }
                .switchMap { searchCoinsWithQuery(it) }
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
                .subscribeOn(Schedulers.io())
                .subscribe(coinListRelay)
                .addTo(disposables)
        }

        private fun refreshCoins() {
            repository.forceRefreshCoinListAndSaveToDb()
                .doOnSubscribe { coinListRelay.accept(listOf(SearchCoinsListItem.Loading)) }
                .doOnError { coinListRelay.accept(listOf()) }
                .subscribeOn(Schedulers.io())
                .subscribeBy(onError = { showNetworkErrorRelay.accept(Unit) })
                .addTo(disposables)
        }

        private fun searchCoinsWithQuery(query: CharSequence): Observable<List<Coin>> =
            if (filterOutOwnedCoins) {
                repository.searchUnownedCoinWithQuery(query)
            } else {
                repository.searchCoinsWithQuery(query)
            }.map { coins ->
                coins.map { coin -> coin.toUi() }
            }

        //region Inputs

        override fun onClickListItem(index: Int) {
            when (val coin = coinListRelay.value?.getOrNull(index)) {
                is SearchCoinsListItem.Coin -> showCoinDetailControllerRelay.accept(coin.symbol)
                is SearchCoinsListItem.RefreshFooter -> refreshCoins()
            }
        }

        override fun setSearchQuery(query: CharSequence) {
            searchQueryRelay.accept(query)
        }

        //endregion

        //region Outputs

        override fun coinList(): Observable<List<SearchCoinsListItem>> =
            coinListRelay.distinctUntilChanged()

        override fun showCoinDetailController(): Observable<String> =
            showCoinDetailControllerRelay

        override fun showNetworkError(): Observable<Unit> =
            showNetworkErrorRelay

        //endregion

        @AssistedInject.Factory
        interface Factory {
            fun create(filterOutOwnedCoins: Boolean, savedState: SavedStateHandle): ViewModel
        }

    }

}
