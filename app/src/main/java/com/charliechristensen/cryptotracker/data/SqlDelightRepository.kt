package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.extensions.flowAsList
import com.charliechristensen.cryptotracker.cryptotracker.Database
import com.charliechristensen.cryptotracker.data.mappers.CoinMappers
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistoryElement
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.models.SymbolPricePair
import com.squareup.sqldelight.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SqlDelightRepository @Inject constructor(
    private val remoteGateway: RemoteGateway,
    database: Database
) : Repository {

    private val coinQueries = database.dbCoinQueries
    private val coinPriceQueries = database.dbCoinPriceDataQueries
    private val portfolioQueries = database.dbPortfolioCoinQueries
    private val combinedTableQueries = database.dbCombinedTableQueries
    private val coinHistoryQueries = database.dbCoinHistoryQueries

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        coinQueries.searchCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun searchCoinsPaged(query: CharSequence, limit: Long, offset: Long): Query<Coin> =
        coinQueries.searchCoinsByNamePaged(query.toString(), limit, offset, CoinMappers.dbCoinMapper)

    override fun getCoinCount(searchQuery: CharSequence): Query<Long> = coinQueries.countCoins(searchQuery.toString())

    override fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        coinQueries.selectBySymbol(symbol, CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>> =
        combinedTableQueries.searchUnownedCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .flowAsList()

    override fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        portfolioQueries.selectUnitsOwnedForSymbol(symbol)
            .flowAsList()

    override fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>> =
        combinedTableQueries.getPortfolioData(CoinMappers.dbCoinWithPriceAndAmountMapper)
            .flowAsList()

    private fun loadCoinPrices(symbol: String): Flow<List<CoinPriceData>> =
        coinPriceQueries.selectBySymbol(symbol, CoinMappers.dbCoinPriceDataMapper)
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
        return loadCoinPrices(symbol)
            .flatMapLatest { dbList ->
                if (dbList.isEmpty() || shouldForceRefresh) {
                    shouldForceRefresh = false
                    flow {
                        emit(dbList)
                        fetchCoinPriceAndSaveToDb(symbol)
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    private suspend fun fetchCoinPriceAndSaveToDb(symbol: String) {
        val remoteCoinPriceData = remoteGateway.getFullCoinPrice(symbol, Constants.DefaultCurrency)
        if (remoteCoinPriceData.rawData?.containsKey(symbol) == true) {
            val rawData = remoteCoinPriceData.rawData!![symbol] ?: return
            if (rawData.containsKey(Constants.DefaultCurrency)) {
                val coinPriceRawData = rawData[Constants.DefaultCurrency] ?: return
                coinPriceQueries.insert(
                    coinPriceRawData.fromSymbol ?: symbol,
                    coinPriceRawData.price ?: 0.0,
                    coinPriceRawData.open24Hour ?: 0.0,
                    coinPriceRawData.high24Hour ?: 0.0,
                    coinPriceRawData.low24Hour ?: 0.0
                )
            }
        }
    }

    override fun addTemporarySubscription(symbol: String, currency: String) {
        remoteGateway.addTemporarySubscription(symbol, currency)
    }

    override fun clearTemporarySubscriptions(currency: String) {
        remoteGateway.clearTemporarySubscriptions(currency)
    }

    override fun connectToLivePrices(symbols: Collection<String>, currency: String) {
        remoteGateway.connect { socket -> socket.setPortfolioSubscriptions(symbols, currency) }
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
                ?.forEach { coinData ->
                    if (coinData.symbol == null || coinData.coinName == null || coinData.sortOrder == null) return@forEach
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

    override suspend fun updatePriceForCoin(coinSymbol: String, price: Double) {
        coinPriceQueries.updatePrice(price, coinSymbol)
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
    ): Flow<List<CoinHistoryElement>> =
        coinHistoryQueries.selectByTimePeriod(
            symbol,
            Constants.DefaultCurrency,
            timePeriod
        ) { _, _, _, _, time, close, high, low, open, volumeFrom, volumeTo ->
            CoinHistoryElement(time, close, high, low, open, volumeFrom, volumeTo)
        }.flowAsList()

    private suspend fun fetchCoinHistoryAndSaveToDb(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod
    ) {
        val historicalData =
            when (timePeriod.timeUnit) {
                CoinHistoryUnits.MINUTE -> remoteGateway.getHistoricalDataByMinute(
                    symbol,
                    Constants.DefaultCurrency,
                    timePeriod.limit
                )
                CoinHistoryUnits.HOUR -> remoteGateway.getHistoricalDataByHour(
                    symbol,
                    Constants.DefaultCurrency,
                    timePeriod.limit
                )
                CoinHistoryUnits.DAY -> remoteGateway.getHistoricalDataByDay(
                    symbol,
                    Constants.DefaultCurrency,
                    timePeriod.limit
                )
            }

        coinHistoryQueries.transaction {
            historicalData.data
                ?.filterNotNull()
                ?.forEach { remoteElement ->
                    if (remoteElement.time == null) return@forEach
                    coinHistoryQueries.insertByTimePeriod(
                        symbol,
                        Constants.DefaultCurrency,
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

}
