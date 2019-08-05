package com.charliechristensen.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.database.models.DbPortfolioCoin
import io.reactivex.Observable

/**
 * Dao for DbPortfolioCoin Data
 */
//@Dao
interface PortfolioCoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCoinToPortfolio(myCoin: DbPortfolioCoin)

    @Query("SELECT * FROM portfolio_coin WHERE symbol = :symbol")
    fun getCoinFromPortfolio(symbol: String): Observable<List<DbPortfolioCoin>>

    @Query("DELETE FROM portfolio_coin WHERE symbol = :symbol")
    fun removeCoinFromPortfolio(symbol: String)

    @Query("SELECT symbol FROM portfolio_coin")
    fun getPortfolioCoinSymbols(): Observable<List<String>>

    @Query("SELECT amountOwned FROM portfolio_coin WHERE symbol = :symbol")
    fun getUnitsOwnedForSymbol(symbol: String): Observable<List<Double>>

}