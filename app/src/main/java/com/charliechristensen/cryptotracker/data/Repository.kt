package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.remote.models.SymbolPricePair
import com.squareup.sqldelight.Query
import kotlinx.coroutines.flow.Flow

interface Repository: AppPreferences {

    fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>>
    fun searchCoinsPaged(query: CharSequence, limit: Int, offset: Int): Query<Coin>
    fun searchUnownedCoinsPaged(query: CharSequence, limit: Int, offset: Int): Query<Coin>
    fun getCoinCount(searchQuery: CharSequence): Query<Long>
    fun getUnownedCoinCount(searchQuery: CharSequence): Query<Long>
    fun getCoinDetails(symbol: String): Flow<List<Coin>>
    fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>>
    fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>>
    fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>>
    fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean = true
    ): Flow<List<CoinPriceData>>

    fun getPortfolioCoinSymbols(): Flow<List<String>>
    fun addTemporarySubscription(symbol: String)
    fun clearTemporarySubscriptions()

    fun setPortfolioSubscriptions(symbols: Collection<String>, newCurrency: String, oldCurrency: String)
    fun disconnectFromLivePrices()
    fun priceUpdateReceived(): Flow<SymbolPricePair>

    suspend fun forceRefreshCoinListAndSaveToDb()
    suspend fun refreshCoinListIfNeeded()
    suspend fun addPortfolioCoin(symbol: String, amountOwned: Double)
    suspend fun removeCoinFromPortfolio(symbol: String)
    suspend fun updatePriceForCoin(coinSymbol: String, currency: String, price: Double)
    fun getCoinHistory(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean = true
    ): Flow<List<CoinHistoryElement>>
}
