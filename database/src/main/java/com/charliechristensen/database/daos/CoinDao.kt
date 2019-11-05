package com.charliechristensen.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.database.models.DbCoin
import kotlinx.coroutines.flow.Flow

/**
 * Dao for DbCoin Data
 */
@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(dbCoin: DbCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(dbCoins: List<DbCoin>)

    @Query("SELECT * FROM coin WHERE symbol = :symbol")
    fun getCoin(symbol: String): Flow<List<DbCoin>>

    @Query("SELECT * FROM coin")
    fun getAllCoins(): Flow<List<DbCoin>>

    @Query(
        "SELECT * FROM coin " +
                "WHERE coinName LIKE '%' || :query || '%' " +
                "ORDER BY sortOrder"
    )
    fun searchCoinsByName(query: String): Flow<List<DbCoin>>

}
