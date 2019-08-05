package com.charliechristensen.database.daos

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.database.models.DbCoin
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Dao for DbCoin Data
 */
//@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(dbCoin: DbCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(dbCoins: List<DbCoin>)

    @Query("SELECT * FROM coin WHERE symbol = :symbol")
    fun getCoin(symbol: String): Observable<List<DbCoin>>

    @Query("SELECT * FROM coin")
    fun getAllCoins(): Single<List<DbCoin>>

    @Query("SELECT * FROM coin")
    fun getAllCoinsPaged(): DataSource.Factory<Int, DbCoin>

    @Query(
        "SELECT * FROM coin " +
                "WHERE coinName LIKE '%' || :query || '%' " +
                "ORDER BY sortOrder"
    )
    fun searchCoinsByName(query: String): Observable<List<DbCoin>>

}