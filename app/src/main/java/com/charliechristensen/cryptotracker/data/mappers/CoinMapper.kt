package com.charliechristensen.cryptotracker.data.mappers

import com.charliechristensen.cryptotracker.data.mappers.CoinMapper.map
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.ui.Coin

/**
 * Created by Chuck on 1/19/2018.
 */
object CoinMapper {

    fun map(dbCoin: DbCoin): Coin {
        return Coin(
            dbCoin.imageUrl,
            dbCoin.symbol,
            dbCoin.coinName
        )
    }

}

fun DbCoin.toUi(): Coin = map(this)