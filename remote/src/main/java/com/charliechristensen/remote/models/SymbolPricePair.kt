package com.charliechristensen.remote.models

data class SymbolPricePair(
    val symbol: String,
    val currency: String,
    val price: Double
)
