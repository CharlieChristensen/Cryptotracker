package com.charliechristensen.cryptotracker.data.models.graph

@Suppress("unused")
class CoinHistoryElement(
    val time: Long,
    val close: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val volumeFrom: Double,
    val volumeTo: Double
)
