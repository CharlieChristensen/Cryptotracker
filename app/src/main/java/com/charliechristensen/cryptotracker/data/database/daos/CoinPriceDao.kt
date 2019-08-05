package com.charliechristensen.cryptotracker.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charliechristensen.cryptotracker.data.models.database.DbCoinPriceData
import io.reactivex.Observable

/**
 * Dao for coin_price_data table
 */
@Dao
interface CoinPriceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setPrice(priceObject: DbCoinPriceData)

    @Query("SELECT * FROM coin_price_data WHERE symbol = :coinSymbol")
    fun getPrice(coinSymbol: String): Observable<List<DbCoinPriceData>>

    @Query("UPDATE coin_price_data SET price = :price WHERE symbol = :coinSymbol")
    fun updatePrice(coinSymbol: String, price: Double)

}