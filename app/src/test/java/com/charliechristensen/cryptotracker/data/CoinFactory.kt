package com.charliechristensen.cryptotracker.data

import com.charliechristensen.database.models.DbCoin
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.database.models.DbCoinWithPriceAndAmount
import com.charliechristensen.database.models.DbPortfolioCoin

/**
 * Creates models for testing
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object CoinFactory {

    fun makeDbCoin(): DbCoin {
        return DbCoin(
            DataFactory.randomSymbol(),
            DataFactory.randomUrl(),
            DataFactory.randomCoinName(),
            DataFactory.randomInt()
        )
    }

    fun makeDbCoin(symbol: String): DbCoin {
        return DbCoin(
            symbol,
            DataFactory.randomUrl(),
            DataFactory.randomCoinName(),
            DataFactory.randomInt()
        )
    }

    fun makeDbCoinList(count: Int): List<DbCoin> {
        val coins = mutableListOf<DbCoin>()
        repeat(count) {
            coins.add(makeDbCoin())
        }
        return coins
    }

    fun makePortfolioCoin(): DbPortfolioCoin {
        return DbPortfolioCoin(
            DataFactory.randomSymbol(),
            DataFactory.randomDouble()
        )
    }

    fun makePortfolioCoin(symbol: String): DbPortfolioCoin {
        return DbPortfolioCoin(
            symbol,
            DataFactory.randomDouble()
        )
    }


    fun makePortfolioCoinList(count: Int): List<DbPortfolioCoin> {
        val coins = mutableListOf<DbPortfolioCoin>()
        repeat(count) {
            coins.add(makePortfolioCoin())
        }
        return coins
    }

    fun makeCoinPriceData(symbol: String): DbCoinPriceData {
        return DbCoinPriceData(
            symbol,
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble()
        )
    }

    fun makeCoinPriceData(): DbCoinPriceData {
        return DbCoinPriceData(
            DataFactory.randomSymbol(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble()
        )
    }

    fun makeCoinPriceDataList(count: Int): List<DbCoinPriceData> {
        val coinPriceList = mutableListOf<DbCoinPriceData>()
        repeat(count) {
            coinPriceList.add(makeCoinPriceData())
        }
        return coinPriceList
    }

    fun makeDbCoinWithPriceAndAmount(symbol: String): DbCoinWithPriceAndAmount {
        return DbCoinWithPriceAndAmount(
            symbol,
            DataFactory.randomUrl(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble()
        )
    }

    fun makeDbCoinWithPriceAndAmount(): DbCoinWithPriceAndAmount {
        return DbCoinWithPriceAndAmount(
            DataFactory.randomSymbol(),
            DataFactory.randomUrl(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble(),
            DataFactory.randomDouble()
        )
    }

    fun makeDbCoinWithPriceAndAmountList(count: Int): List<DbCoinWithPriceAndAmount> {
        val list = mutableListOf<DbCoinWithPriceAndAmount>()
        repeat(count) {
            list.add(makeDbCoinWithPriceAndAmount())
        }
        return list
    }

}
