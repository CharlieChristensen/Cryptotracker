package com.charliechristensen.cryptotracker.data.models.graph

import com.charliechristensen.remote.models.ServerHistoryElement


@Suppress("unused")
/**
 * Created by Chuck on 12/31/2017.
 */
class CoinHistoryElement(val time: Long,
                         val close: Double,
                         val high: Double,
                         val low: Double,
                         val open: Double,
                         val volumeFrom: Double,
                         val volumeTo: Double){
    constructor(serverHistoryElement: ServerHistoryElement): this(
            serverHistoryElement.time,
            serverHistoryElement.close,
            serverHistoryElement.high,
            serverHistoryElement.low,
            serverHistoryElement.open,
            serverHistoryElement.volumeFrom,
            serverHistoryElement.volumeTo
    )
}
