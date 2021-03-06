package com.charliechristensen.coinlist

import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.data.Repository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchCoinsInteractor @Inject constructor(
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
}
