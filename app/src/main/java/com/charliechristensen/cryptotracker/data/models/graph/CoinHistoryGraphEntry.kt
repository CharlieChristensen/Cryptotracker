package com.charliechristensen.cryptotracker.data.models.graph

import com.github.mikephil.charting.data.Entry

/**
 * Created by Chuck on 1/2/2018.
 */
class CoinHistoryGraphEntry(x: Float, y: Float) : Entry(x, y) {
    constructor(coinHistoryElement: CoinHistoryElement) : this(
        coinHistoryElement.time.toFloat(),
        coinHistoryElement.close.toFloat()
    )
}

