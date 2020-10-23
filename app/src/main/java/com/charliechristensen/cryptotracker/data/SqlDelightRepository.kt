package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.extensions.flowAsList
import com.charliechristensen.cryptotracker.cryptotracker.Database
import com.charliechristensen.cryptotracker.data.mappers.CoinMappers
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.models.SymbolPricePair
import com.squareup.sqldelight.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

class SqlDelightRepository constructor(
    private val remoteGateway: RemoteGateway,
    private val appPreferences: AppPreferences,
    database: Database
) : Repository, AppPreferences by appPreferences {

    private val coinQueries = database.dbCoinQueries
    private val coinPriceQueries = database.dbCoinPriceDataQueries
    private val portfolioQueries = database.dbPortfolioCoinQueries
    private val combinedTableQueries = database.dbCombinedTableQueries
    private val coinHistoryQueries = database.dbCoinHistoryQueries

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        coinQueries.searchCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun searchCoinsPaged(
        query: CharSequence,
        limit: Int,
        offset: Int
    ): Query<Coin> =
        coinQueries.searchCoinsByNamePaged(
            query.toString(),
            limit.toLong(),
            offset.toLong(),
            CoinMappers.dbCoinMapper
        )

    override fun searchUnownedCoinsPaged(
        query: CharSequence,
        limit: Int,
        offset: Int
    ): Query<Coin> =
        combinedTableQueries.searchUnownedCoinsByNamePaged(
            query.toString(),
            limit.toLong(),
            offset.toLong(),
            CoinMappers.dbCoinMapper
        )

    override fun getCoinCount(searchQuery: CharSequence): Query<Long> =
        coinQueries.countCoins(searchQuery.toString())

    override fun getUnownedCoinCount(searchQuery: CharSequence): Query<Long> =
        combinedTableQueries.countUnownedCoins(searchQuery.toString())

    override fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        coinQueries.selectBySymbol(symbol, CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>> =
        combinedTableQueries.searchUnownedCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        portfolioQueries.selectUnitsOwnedForSymbol(symbol)
            .flowAsList()

    override fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>> = combine(
        getPortfolioCoinSymbols(),
        getPortfolio()
    ) { symbols, portfolio ->
        if(symbols.isNotEmpty() && portfolio.isEmpty()) {
            fetchCoinPriceAndSaveToDb(symbols, getCurrency())
        }
        portfolio
    }

    private fun getPortfolio(): Flow<List<CoinWithPriceAndAmount>> =
        combinedTableQueries.getPortfolioData(
            getCurrency(),
            CoinMappers.dbCoinWithPriceAndAmountMapper
        ).flowAsList()

    private fun loadCoinPrices(symbol: String, currency: String): Flow<List<CoinPriceData>> =
        coinPriceQueries.selectBySymbol(symbol, currency, CoinMappers.dbCoinPriceDataMapper)
            .flowAsList()

    override fun getPortfolioCoinSymbols(): Flow<List<String>> =
        portfolioQueries.selectAllSymbols()
            .flowAsList()

    @ExperimentalCoroutinesApi
    override fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean
    ): Flow<List<CoinPriceData>> {
        var shouldForceRefresh = forceRefresh
        return loadCoinPrices(symbol, getCurrency())
            .flatMapLatest { dbList ->
                if (dbList.isEmpty() || shouldForceRefresh) {
                    shouldForceRefresh = false
                    flow {
                        emit(dbList)
                        fetchCoinPriceAndSaveToDb(symbol, getCurrency())
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    private suspend fun fetchCoinPriceAndSaveToDb(symbol: String, currency: String) {
        val remoteCoinPriceData = remoteGateway.getFullCoinPrice(symbol, currency)
        if (remoteCoinPriceData.rawData?.containsKey(symbol) == true) {
            val rawData = remoteCoinPriceData.rawData!![symbol] ?: return
            if (rawData.containsKey(currency)) {
                val coinPriceRawData = rawData[currency] ?: return
                coinPriceQueries.insert(
                    coinPriceRawData.fromSymbol ?: symbol,
                    coinPriceRawData.toSymbol ?: currency,
                    coinPriceRawData.price ?: 0.0,
                    coinPriceRawData.open24Hour ?: 0.0,
                    coinPriceRawData.high24Hour ?: 0.0,
                    coinPriceRawData.low24Hour ?: 0.0
                )
            }
        }
    }

    private suspend fun fetchCoinPriceAndSaveToDb(symbols: List<String>, currency: String) {
        val remoteCoinPriceData =
            remoteGateway.getFullCoinPrice(symbols.joinToString(","), currency)
        remoteCoinPriceData.rawData?.forEach { (symbol, rawData) ->
            if (rawData.containsKey(currency)) {
                val coinPriceRawData = rawData[currency] ?: return
                coinPriceQueries.insert(
                    coinPriceRawData.fromSymbol ?: symbol ?: return@forEach,
                    coinPriceRawData.toSymbol ?: currency,
                    coinPriceRawData.price ?: 0.0,
                    coinPriceRawData.open24Hour ?: 0.0,
                    coinPriceRawData.high24Hour ?: 0.0,
                    coinPriceRawData.low24Hour ?: 0.0
                )
            }
        }
    }

    override fun addTemporarySubscription(symbol: String) {
        remoteGateway.addTemporarySubscription(symbol, getCurrency())
    }

    override fun clearTemporarySubscriptions() {
        remoteGateway.clearTemporarySubscriptions(getCurrency())
    }

    override fun setPortfolioSubscriptions(symbols: Collection<String>, newCurrency: String, oldCurrency: String) {
        remoteGateway.setPortfolioSubscriptions(symbols, newCurrency, oldCurrency)
    }

    override fun disconnectFromLivePrices() {
        remoteGateway.disconnect()
    }

    override fun priceUpdateReceived(): Flow<SymbolPricePair> =
        remoteGateway.priceUpdateReceived()

    override suspend fun forceRefreshCoinListAndSaveToDb() {
        val serverCoinList = remoteGateway.getCoinList()
        val baseImageUrl = serverCoinList.baseImageUrl
        coinQueries.transaction {
            serverCoinList.data?.values
                ?.filterNotNull()
                ?.filter { coinData -> coinData.symbol != null && coinData.coinName != null && coinData.sortOrder != null }
                ?.forEach { coinData ->
                    coinQueries.insert(
                        coinData.symbol!!,
                        baseImageUrl + coinData.imageUrl,
                        coinData.coinName!!,
                        coinData.sortOrder!!.toLong()
                    )
                }
        }
    }

    override suspend fun refreshCoinListIfNeeded() {
        val coinList = coinQueries.selectAll().executeAsList()
        if (coinList.isEmpty()) {
            forceRefreshCoinListAndSaveToDb()
        }
    }

    override suspend fun addPortfolioCoin(symbol: String, amountOwned: Double) {
        portfolioQueries.insert(symbol, amountOwned)
    }

    override suspend fun removeCoinFromPortfolio(symbol: String) {
        portfolioQueries.deleteBySymbol(symbol)
    }

    override suspend fun updatePriceForCoin(coinSymbol: String, currency: String, price: Double) {
        coinPriceQueries.updatePrice(price, coinSymbol, currency)
    }

    @ExperimentalCoroutinesApi
    override fun getCoinHistory(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean
    ): Flow<List<CoinHistoryElement>> {
        var shouldRefresh = forceRefresh
        return getCoinHistoryFromDb(symbol, timePeriod)
            .flatMapLatest { dbList ->
                if (dbList.isEmpty() || shouldRefresh) {
                    shouldRefresh = false
                    flow {
                        if (dbList.isNotEmpty()) {
                            emit(dbList)
                        }
                        fetchCoinHistoryAndSaveToDb(symbol, timePeriod)
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    private fun getCoinHistoryFromDb(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod
    ): Flow<List<CoinHistoryElement>> = coinHistoryQueries.selectByTimePeriod(
        symbol,
        getCurrency(),
        timePeriod,
        CoinMappers.dbCoinHistoryMapper
    ).flowAsList()

    private suspend fun fetchCoinHistoryAndSaveToDb(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod
    ) {

        val topCoinFullData = remoteGateway.getTopCoinFullData("USD")
        Timber.d(topCoinFullData.toString())
        val historicalData =
            when (timePeriod.timeUnit) {
                CoinHistoryUnits.MINUTE -> remoteGateway.getHistoricalDataByMinute(
                    symbol,
                    getCurrency(),
                    timePeriod.limit
                )
                CoinHistoryUnits.HOUR -> remoteGateway.getHistoricalDataByHour(
                    symbol,
                    getCurrency(),
                    timePeriod.limit
                )
                CoinHistoryUnits.DAY -> remoteGateway.getHistoricalDataByDay(
                    symbol,
                    getCurrency(),
                    timePeriod.limit
                )
            }

        coinHistoryQueries.transaction {
            historicalData.data
                ?.filterNotNull()
                ?.filter { it.time != null }
                ?.forEach { remoteElement ->
                    coinHistoryQueries.insertByTimePeriod(
                        symbol,
                        getCurrency(),
                        timePeriod,
                        remoteElement.time!!,
                        remoteElement.close ?: 0.0,
                        remoteElement.high ?: 0.0,
                        remoteElement.low ?: 0.0,
                        remoteElement.open ?: 0.0,
                        remoteElement.volumeFrom ?: 0.0,
                        remoteElement.volumeTo ?: 0.0
                    )
                }
        }
    }

    override fun setCurrency(symbol: String) {
        appPreferences.setCurrency(symbol)
    }

}
