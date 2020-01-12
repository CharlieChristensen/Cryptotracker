package com.charliechristensen.coindetail.data

import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

/**
 * Created by Chuck on 1/16/2018.
 */
sealed class CoinDetailGraphState {
    data class Success(
        val coinHistoryList: List<CoinHistoryGraphEntry>,
        val color: ValueChangeColor,
        val startingPrice: Double
    ) : CoinDetailGraphState()

    object Loading : CoinDetailGraphState()
    object NoData : CoinDetailGraphState()
    object Error : CoinDetailGraphState()
}
