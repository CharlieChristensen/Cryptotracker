package com.charliechristensen.database

import com.charliechristensen.database.daos.CoinDao
import com.charliechristensen.database.daos.CoinPriceDao
import com.charliechristensen.database.daos.CombinedTableDao
import com.charliechristensen.database.daos.PortfolioCoinDao
import javax.inject.Inject

internal class DatabaseApiImpl @Inject constructor(
    private val database: AppDatabase
) : DatabaseApi,
    CoinDao by database.coinDao(),
    CoinPriceDao by database.coinPriceDao(),
    CombinedTableDao by database.combinedTableDao(),
    PortfolioCoinDao by database.portfolioCoinDao()
