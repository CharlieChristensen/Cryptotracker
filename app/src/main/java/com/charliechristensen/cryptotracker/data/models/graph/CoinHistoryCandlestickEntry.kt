package com.charliechristensen.cryptotracker.data.models.graph

import com.github.mikephil.charting.data.CandleEntry

class CoinHistoryCandlestickEntry(
    xPos: Float,
    shadowH: Float,
    shadowL: Float,
    open: Float,
    close: Float
) : CandleEntry(xPos, shadowH, shadowL, open, close)