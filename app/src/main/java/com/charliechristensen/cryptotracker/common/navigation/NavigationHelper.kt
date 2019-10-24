package com.charliechristensen.cryptotracker.common.navigation

import android.net.Uri
import androidx.core.net.toUri

object NavigationHelper {

    fun portfolioUri(): Uri = "cryptotracker://portfolio".toUri()
    fun rootCoinListUri(): Uri = "cryptotracker://coinsRoot".toUri()
    fun coinListUri(filterOwnedCoins: Boolean): Uri = "cryptotracker://coins/${filterOwnedCoins}".toUri()
    fun coinDetailUri(coinSymbol: String): Uri = "cryptotracker://coin/$coinSymbol".toUri()
    fun settingsUri(): Uri = "cryptotracker://settings".toUri()

}