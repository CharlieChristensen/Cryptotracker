package com.charliechristensen.cryptotracker.data.models.ui

import com.charliechristensen.cryptotracker.cryptotracker.R

/**
 * Encapsulates Time Period Data
 */
sealed class CoinHistoryTimePeriod(
    val timeUnit: CoinHistoryUnits,
    val limit: Int,
    val displayString: Int
) {
    object OneDay : CoinHistoryTimePeriod(CoinHistoryUnits.MINUTE, 1440, R.string._24h_change)
    object OneWeek : CoinHistoryTimePeriod(CoinHistoryUnits.HOUR, 168, R.string._1w_change)
    object OneMonth : CoinHistoryTimePeriod(CoinHistoryUnits.HOUR, 720, R.string._1m_change)
    object ThreeMonth : CoinHistoryTimePeriod(
        CoinHistoryUnits.DAY, 90, R.string._3m_change)
    object SixMonth : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 180, R.string._6m_change)
    object OneYear : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 365, R.string._1Y_change)
    object All : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 2000, R.string._all_time)

    companion object {
        fun getTimePeriodWithIndex(index: Int): CoinHistoryTimePeriod {
            return when (index) {
                0 -> {
                    OneDay
                }
                1 -> {
                    OneWeek
                }
                2 -> {
                    OneMonth
                }
                3 -> {
                    ThreeMonth
                }
                4 -> {
                    SixMonth
                }
                5 -> {
                    OneYear
                }
                else -> {
                    All
                }
            }
        }
    }
}

enum class CoinHistoryUnits {
    MINUTE,
    HOUR,
    DAY
}
