package com.charliechristensen.cryptotracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.charliechristensen.cryptotracker.data.database.daos.CoinDao
import com.charliechristensen.cryptotracker.data.database.daos.CoinPriceDao
import com.charliechristensen.cryptotracker.data.database.daos.CombinedTableDao
import com.charliechristensen.cryptotracker.data.database.daos.PortfolioCoinDao
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.database.DbCoinPriceData
import com.charliechristensen.cryptotracker.data.models.database.DbPortfolioCoin

/**
 * Application Database
 */
@Database(
    entities = [(DbCoin::class), (DbCoinPriceData::class), (DbPortfolioCoin::class)],
    version = 26
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun coinPriceDao(): CoinPriceDao
    abstract fun portfolioCoinDao(): PortfolioCoinDao
    abstract fun combinedTableDao(): CombinedTableDao
}
