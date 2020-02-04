package com.charliechristensen.cryptotracker.data.models.graph

import com.charliechristensen.remote.models.RemoteHistoryElement

@Suppress("unused")
class CoinHistoryElement(
    val time: Long,
    val close: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val volumeFrom: Double,
    val volumeTo: Double
) {
    constructor(remoteHistoryElement: RemoteHistoryElement) : this(
            remoteHistoryElement.time,
            remoteHistoryElement.close,
            remoteHistoryElement.high,
            remoteHistoryElement.low,
            remoteHistoryElement.open,
            remoteHistoryElement.volumeFrom,
            remoteHistoryElement.volumeTo
    )
}
