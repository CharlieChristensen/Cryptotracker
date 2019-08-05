package com.charliechristensen.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Chuck on 1/3/2018.
 */
//@Entity(tableName = "portfolio_coin")
data class DbPortfolioCoin(
    @PrimaryKey val symbol: String,
    val amountOwned: Double
)