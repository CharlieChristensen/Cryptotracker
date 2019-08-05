package com.charliechristensen.database

import androidx.room.Room
import android.content.Context

/**
 * Container for the database. Allows to remove room dependencies from the app module
 */
class Database(applicationContext: Context) {

    private val appDatabase =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "coin-database")
            .fallbackToDestructiveMigration()
            .build()

    fun coinDao() =
        appDatabase.coinDao()

    fun coinPriceDao() =
        appDatabase.coinPriceDao()

    fun portfolioCoinDao() =
        appDatabase.portfolioCoinDao()

    fun combinedTableDao() =
        appDatabase.combinedTableDao()

}