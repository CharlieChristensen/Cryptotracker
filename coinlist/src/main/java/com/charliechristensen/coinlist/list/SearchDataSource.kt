package com.charliechristensen.coinlist.list

import androidx.paging.PagingSource
import androidx.paging.PositionalDataSource
import com.charliechristensen.coinlist.SearchCoinsInteractor
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.squareup.sqldelight.Query

class SearchDataSource(
    private val interactor: SearchCoinsInteractor,
    private val searchQuery: String,
    private val filterOutOwnedCoins: Boolean
) : PagingSource<Int, SearchCoinsListItem>(), Query.Listener {

    private var dbQuery: Query<Coin>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchCoinsListItem> {
        try {
            val pageNumber = params.key ?: 0
            val offset = params.loadSize * pageNumber
            val data = loadRange(limit = params.loadSize, offset = offset)
            return LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = pageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

//    override fun loadInitial(
//        params: LoadInitialParams,
//        callback: LoadInitialCallback<SearchCoinsListItem>
//    ) {
//        val totalCount = interactor.getCoinCount(searchQuery, filterOutOwnedCoins).executeAsOne().toInt()
//        val offset = computeInitialLoadPosition(params, totalCount)
//        val limit = computeInitialLoadSize(params, offset, totalCount)
//        val results = loadRange(limit, offset)
//        if (!isInvalid) {
//            if (results.isEmpty()) {
//                callback.onResult(listOf(SearchCoinsListItem.RefreshFooter), 0, 1)
//            } else {
//                callback.onResult(results, offset, totalCount)
//            }
//        }
//    }

//    override fun loadRange(
//        params: LoadRangeParams,
//        callback: LoadRangeCallback<SearchCoinsListItem>
//    ) {
//        if (!isInvalid) {
//            callback.onResult(loadRange(params.loadSize, params.startPosition))
//        }
//    }

    private fun loadRange(limit: Int, offset: Int): List<SearchCoinsListItem> {
        dbQuery?.removeListener(this)
        return interactor.searchCoinsPaged(searchQuery, limit, offset, filterOutOwnedCoins)
            .also { dbQuery ->
                dbQuery.addListener(this)
                this.dbQuery = dbQuery
            }
            .executeAsList()
            .map { coin ->
                SearchCoinsListItem.Coin(
                    coin.coinName,
                    coin.symbol,
                    coin.imageUrl ?: ""
                )
            }
    }

    override fun queryResultsChanged() = invalidate()

}
