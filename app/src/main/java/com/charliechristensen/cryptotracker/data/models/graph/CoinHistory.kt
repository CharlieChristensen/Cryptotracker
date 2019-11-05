package com.charliechristensen.cryptotracker.data.models.graph

import com.charliechristensen.remote.models.ServerHistoryResponse


/**
 * Created by Chuck on 12/31/2017.
 */
data class CoinHistory(val historyElements: List<CoinHistoryElement>) {

    constructor(serverHistoryResponse: ServerHistoryResponse): this(
            serverHistoryResponse.data.map {
                CoinHistoryElement(
                    it
                )
            }
    )
}
