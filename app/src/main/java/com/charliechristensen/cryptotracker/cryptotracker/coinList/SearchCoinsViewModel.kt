package com.charliechristensen.cryptotracker.cryptotracker.coinList

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
        fun searchCoinsWithQuery(query: CharSequence): Observable<List<Coin>>
        fun setSearchQuery(query: CharSequence)
    }

    interface Outputs {
        fun coinList(): Observable<List<SearchCoinsListItem>>
        fun showCoinDetailController(): Observable<String>
        fun showNetworkError(): Observable<Unit>
    }

    class ViewModel @AssistedInject constructor(
        private val repository: Repository,
        @Assisted private val filterOutOwnedCoins: Boolean
    ) : BaseViewModel(), Inputs, Outputs {

        private val showCoinDetailControllerRelay: PublishRelay<String> = PublishRelay.create()
        private val searchQueryRelay: BehaviorRelay<CharSequence> = BehaviorRelay.createDefault("")
        private val showNetworkErrorRelay: PublishRelay<Unit> = PublishRelay.create()
        private val coinListRelay: BehaviorRelay<List<SearchCoinsListItem>> =
            BehaviorRelay.createDefault(listOf(SearchCoinsListItem.Loading))

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            searchQueryRelay
                .debounce(400, TimeUnit.MILLISECONDS)
                .switchMap { searchCoinsWithQuery(it) }
                .subscribeOn(Schedulers.io())
                .map { coinList ->
                    coinList.map { coin ->
                        SearchCoinsListItem.Coin(coin.coinName, coin.symbol, coin.imageUrl ?: "")
                    }.plus(SearchCoinsListItem.RefreshFooter)
                }
                .subscribe(coinListRelay)
                .addTo(disposables)
        }

        //region Inputs

        override fun onClickListItem(index: Int) {
            when (val coin = coinListRelay.value?.getOrNull(index)) {
                is SearchCoinsListItem.Coin -> showCoinDetailControllerRelay.accept(coin.symbol)
                is SearchCoinsListItem.RefreshFooter -> refreshCoins()
            }
        }

        override fun searchCoinsWithQuery(query: CharSequence): Observable<List<Coin>> =
            if (filterOutOwnedCoins) {
                repository.searchUnownedCoinWithQuery(query)
            } else {
                repository.searchCoinsWithQuery(query)
            }.map { coins ->
                coins.map { coin -> coin.toUi() }
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

        private fun refreshCoins() {
            repository.forceRefreshCoinListAndSaveToDb()
                .doOnSubscribe { coinListRelay.accept(listOf(SearchCoinsListItem.Loading)) }
                .subscribeOn(Schedulers.io())
                .subscribeBy(onError = { showNetworkErrorRelay.accept(Unit) })
                .addTo(disposables)
        }

        @AssistedInject.Factory
        interface Factory {
            fun create(filterOutOwnedCoins: Boolean): ViewModel
        }

    }

}