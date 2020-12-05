package com.charliechristensen.coinlist

import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.squareup.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class SearchCoinsInteractor constructor(
    private val repository: Repository
) {

    fun searchCoinsWithQuery(
        query: CharSequence,
        filterOutOwnedCoins: Boolean
    ): Flow<List<SearchCoinsListItem>> =
        if (filterOutOwnedCoins) {
            repository.searchUnownedCoinWithQuery(query)
        } else {
            repository.searchCoinsWithQuery(query)
        }.map { coinList ->
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

    suspend fun forceRefreshCoinListAndSaveToDb() {
        repository.forceRefreshCoinListAndSaveToDb()
    }

    fun getCoinCount(searchQuery: CharSequence, filterOutOwnedCoins: Boolean): Query<Long> =
        if (filterOutOwnedCoins) {
            repository.getUnownedCoinCount(searchQuery)
        } else {
            repository.getCoinCount(searchQuery)
        }

    fun searchCoinsPaged(
        query: CharSequence,
        limit: Int,
        offset: Int,
        filterOutOwnedCoins: Boolean
    ): Query<Coin> =
        if (filterOutOwnedCoins) {
            repository.searchUnownedCoinsPaged(query, limit, offset)
        } else {
            repository.searchCoinsPaged(query, limit, offset)
        }
}
