package com.charliechristensen.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "portfolio_coin")
data class DbPortfolioCoin(
    @PrimaryKey val symbol: String,
    val amountOwned: Double
)
