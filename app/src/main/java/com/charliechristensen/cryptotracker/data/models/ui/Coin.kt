package com.charliechristensen.cryptotracker.data.models.ui

/**
 * Coin to be displayed in the Search Coins View
 */
data class Coin(
    val imageUrl: String?,
    val symbol: String,
    val coinName: String
)
