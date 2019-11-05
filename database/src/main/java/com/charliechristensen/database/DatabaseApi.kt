package com.charliechristensen.database

import com.charliechristensen.database.daos.CoinDao
import com.charliechristensen.database.daos.CoinPriceDao
import com.charliechristensen.database.daos.CombinedTableDao
import com.charliechristensen.database.daos.PortfolioCoinDao

interface DatabaseApi: CoinDao, CoinPriceDao, CombinedTableDao, PortfolioCoinDao
