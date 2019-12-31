package com.charliechristensen.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_price_data")
class DbCoinPriceData(
    @PrimaryKey val symbol: String,
    val price: Double = 0.0,
    val open24Hour: Double = 0.0,
    val high24Hour: Double = 0.0,
    val low24Hour: Double = 0.0
)
