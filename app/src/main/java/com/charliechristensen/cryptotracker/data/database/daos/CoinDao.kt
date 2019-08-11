package com.charliechristensen.cryptotracker.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import io.reactivex.Observable

/**
 * Dao for DbCoin Data
 */
@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(dbCoin: DbCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(dbCoins: List<DbCoin>)

    @Query("SELECT * FROM coin WHERE symbol = :symbol")
    fun getCoin(symbol: String): Observable<List<DbCoin>>

    @Query("SELECT * FROM coin")
    fun getAllCoins(): Observable<List<DbCoin>>

    @Query(
        "SELECT * FROM coin " +
                "WHERE coinName LIKE '%' || :query || '%' " +
                "ORDER BY sortOrder"
    )
    fun searchCoinsByName(query: String): Observable<List<DbCoin>>

    @Query(
        "SELECT * FROM coin " +
                "WHERE coinName LIKE '%' || :query || '%' " +
                "ORDER BY sortOrder LIMIT :limit OFFSET :offset"
    )
    fun searchCoinsByName(query: String, limit: Int, offset: Int): Observable<List<DbCoin>>

}