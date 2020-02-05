package com.charliechristensen.coinlist

import com.charliechristensen.coinlist.list.SearchCoinsListItem
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.squareup.sqldelight.Query
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
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

    fun getCoinCount(searchQuery: CharSequence): Query<Long> = repository.getCoinCount(searchQuery)

    fun searchCoinsPaged(query: CharSequence, limit: Long, offset: Long): Query<Coin> =
        repository.searchCoinsPaged(query, limit, offset)

}
