package com.charliechristensen.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.charliechristensen.database.daos.CoinDao
import com.charliechristensen.database.daos.CoinPriceDao
import com.charliechristensen.database.daos.CombinedTableDao
import com.charliechristensen.database.daos.PortfolioCoinDao
import com.charliechristensen.database.models.DbCoin
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.database.models.DbPortfolioCoin

/**
 * Application Database
 */
//@Database(
//    entities = [(DbCoin::class), (DbCoinPriceData::class), (DbPortfolioCoin::class)],
//    version = 26
//)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun coinPriceDao(): CoinPriceDao
    abstract fun portfolioCoinDao(): PortfolioCoinDao
    abstract fun combinedTableDao(): CombinedTableDao
}
