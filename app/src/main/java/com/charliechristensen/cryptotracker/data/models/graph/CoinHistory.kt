package com.charliechristensen.cryptotracker.data.models.graph

import com.charliechristensen.remote.models.RemoteHistoryResponse

/**
 * Created by Chuck on 12/31/2017.
 */
data class CoinHistory(val historyElements: List<CoinHistoryElement>) {

    constructor(remoteHistoryResponse: RemoteHistoryResponse) : this(
            remoteHistoryResponse.data.map {
                CoinHistoryElement(
                    it
                )
            }
    )
}
