package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.extensions.flowAsList
import com.charliechristensen.cryptotracker.cryptotracker.Database
import com.charliechristensen.cryptotracker.data.mappers.CoinMappers
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.models.SymbolPricePair
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

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        coinQueries.searchCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .flowAsList()

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
                if ((dbList.isEmpty() || shouldForceRefresh)) {
                    shouldForceRefresh = false
                    flow {
                        emit(dbList)
                        fetchCoinPricesAndSaveToDb(symbol)
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    private suspend fun fetchCoinPricesAndSaveToDb(symbol: String) {
        val remoteCoinPriceData = remoteGateway.getFullCoinPrice(symbol, Constants.DefaultCurrency)
        if (remoteCoinPriceData.rawData?.containsKey(symbol) == true) {
            val rawData = remoteCoinPriceData.rawData!![symbol] ?: return
            if (rawData.containsKey(Constants.DefaultCurrency)) {
                val coinPriceRawData = rawData[Constants.DefaultCurrency] ?: return
                coinPriceQueries.insert(
                    coinPriceRawData.fromSymbol,
                    coinPriceRawData.price,
                    coinPriceRawData.open24Hour,
                    coinPriceRawData.high24Hour,
                    coinPriceRawData.low24Hour
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
            serverCoinList.data.values.forEach { coinData ->
                coinQueries.insert(
                    coinData.symbol,
                    baseImageUrl + coinData.imageUrl,
                    coinData.coinName,
                    coinData.sortOrder.toLong()
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

    override suspend fun getHistoricalDataForCoin(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean
    ): CoinHistory {
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
        return CoinHistory(historicalData)
    }
}

//@ExperimentalCoroutinesApi
//override fun getCoinPriceData(
//    symbol: String,
//    forceRefresh: Boolean
//): Flow<List<CoinPriceData>> =
//    object : RxNetworkBoundResource<DbCoinPriceData, RemoteCoinPriceData, CoinPriceData>() {
//        override suspend fun saveToDb(data: List<DbCoinPriceData>) {
//            if (data.isNotEmpty()) {
//                val dbCoinPriceData = data[0]
//                coinPriceQueries.insert(
//                    dbCoinPriceData.symbol,
//                    dbCoinPriceData.price,
//                    dbCoinPriceData.open24Hour,
//                    dbCoinPriceData.high24Hour,
//                    dbCoinPriceData.low24Hour
//                )
//            } else {
//                coinPriceQueries.insertSymbol(symbol)
//            }
//        }
//
//        override fun shouldFetch(data: List<DbCoinPriceData>): Boolean =
//            data.isEmpty() || forceRefresh
//
//        override fun mapToDbType(value: RemoteCoinPriceData): List<DbCoinPriceData> {
//            if (value.rawData?.containsKey(symbol) == true) {
//                val rawData = value.rawData!![symbol] ?: return emptyList()
//                if (rawData.containsKey(Constants.MyCurrency)) {
//                    val coinPriceRawData = rawData[Constants.MyCurrency] ?: return emptyList()
//                    val coinPriceData =
//                        NetworkToDbMapper.mapSqlDelightCoinPriceData(coinPriceRawData)
//                    return listOf(coinPriceData)
//                }
//            }
//            return emptyList()
//        }
//
//        override fun loadFromDb(): Flow<List<DbCoinPriceData>> =
//            coinPriceQueries.selectBySymbol(symbol)
//                .flowAsList()
//
//        override suspend fun loadFromNetwork(): RemoteCoinPriceData =
//            service.getFullCoinPrice(symbol, Constants.MyCurrency)
//
//        override fun mapToUiType(value: DbCoinPriceData): CoinPriceData = CoinPriceData(
//            value.symbol,
//            value.price,
//            value.open24Hour,
//            value.high24Hour,
//            value.low24Hour
//        )
//    }.flow
