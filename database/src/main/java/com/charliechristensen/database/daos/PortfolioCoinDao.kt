package com.charliechristensen.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.database.models.DbPortfolioCoin
import kotlinx.coroutines.flow.Flow

/**
 * Dao for DbPortfolioCoin Data
 */
@Dao
interface PortfolioCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCoinToPortfolio(myCoin: DbPortfolioCoin)

    @Query("DELETE FROM portfolio_coin WHERE symbol = :symbol")
    suspend fun removeCoinFromPortfolio(symbol: String)

    @Query("SELECT * FROM portfolio_coin WHERE symbol = :symbol")
    fun getCoinFromPortfolio(symbol: String): Flow<List<DbPortfolioCoin>>

    @Query("SELECT symbol FROM portfolio_coin")
    fun getPortfolioCoinSymbols(): Flow<List<String>>

    @Query("SELECT amountOwned FROM portfolio_coin WHERE symbol = :symbol")
    fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>>
}
