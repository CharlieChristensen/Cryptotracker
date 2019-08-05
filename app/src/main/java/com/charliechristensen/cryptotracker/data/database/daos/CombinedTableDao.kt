package com.charliechristensen.cryptotracker.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import io.reactivex.Observable

/**
 * Dao for joining tables
 */
@Dao
interface CombinedTableDao {

    @Query(
        "SELECT * FROM coin " +
                "WHERE NOT EXISTS " +
                "(SELECT NULL FROM portfolio_coin WHERE coin.symbol = portfolio_coin.symbol) AND " +
                "coin.coinName LIKE '%' || :query || '%' "+
                "ORDER BY sortOrder"
    )
    fun searchUnownedCoinsByName(query: String): Observable<List<DbCoin>>

    @Query(
        "SELECT coin.symbol, coin.imageUrl, coin_price_data.price, coin_price_data.open24Hour, portfolio_coin.amountOwned " +
                "FROM portfolio_coin " +
                "INNER JOIN coin ON coin.symbol = portfolio_coin.symbol " +
                "INNER JOIN coin_price_data ON coin_price_data.symbol = portfolio_coin.symbol"
    )
    fun getPortfolioData(): Observable<List<DbCoinWithPriceAndAmount>>

}