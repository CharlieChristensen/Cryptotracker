package com.charliechristensen.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * DbCoin model
 */
//@Entity(tableName = "coin", indices = [(Index(value = ["sortOrder"], name = "idx"))])
data class DbCoin(
    @PrimaryKey val symbol: String,
    val imageUrl: String?,
    val coinName: String,
    val sortOrder: Int
)