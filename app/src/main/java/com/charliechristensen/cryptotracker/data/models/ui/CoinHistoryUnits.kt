package com.charliechristensen.cryptotracker.data.models.ui

import com.charliechristensen.cryptotracker.cryptotracker.R
import com.squareup.sqldelight.ColumnAdapter

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
    object ThreeMonth : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 90, R.string._3m_change)
    object SixMonth : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 180, R.string._6m_change)
    object OneYear : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 365, R.string._1Y_change)
    object All : CoinHistoryTimePeriod(CoinHistoryUnits.DAY, 2000, R.string._all_time)

    companion object {

        val databaseAdapter = object : ColumnAdapter<CoinHistoryTimePeriod, Long> {
            override fun decode(databaseValue: Long): CoinHistoryTimePeriod = getTimePeriodWithIndex(databaseValue.toInt())
            override fun encode(value: CoinHistoryTimePeriod): Long = getIndexWithTimePeriod(value).toLong()
        }

        fun encodeToLong(value: CoinHistoryTimePeriod): Long = getIndexWithTimePeriod(value).toLong()

        fun decode(databaseValue: Long): CoinHistoryTimePeriod = getTimePeriodWithIndex(databaseValue.toInt())

        fun getTimePeriodWithIndex(index: Int): CoinHistoryTimePeriod = when (index) {
            0 -> OneDay
            1 -> OneWeek
            2 -> OneMonth
            3 -> ThreeMonth
            4 -> SixMonth
            5 -> OneYear
            else -> All
        }

        fun getIndexWithTimePeriod(index: CoinHistoryTimePeriod): Int = when (index) {
            OneDay -> 0
            OneWeek -> 1
            OneMonth -> 2
            ThreeMonth -> 3
            SixMonth -> 4
            OneYear -> 5
            else -> 6
        }
    }
}

enum class CoinHistoryUnits {
    MINUTE,
    HOUR,
    DAY
}
