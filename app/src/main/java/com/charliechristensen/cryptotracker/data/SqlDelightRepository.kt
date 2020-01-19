package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.RxNetworkBoundResource
import com.charliechristensen.cryptotracker.data.mappers.CoinMappers
import com.charliechristensen.cryptotracker.data.mappers.NetworkToDbMapper
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.database.Database
import com.charliechristensen.database.models.sqldelight.DbCoinPriceData
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.SymbolPricePair
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SqlDelightRepository @Inject constructor(
    private val service: CryptoService,
    private val webSocket: WebSocketService,
    private val database: Database
) : Repository {

    private val coinQueries = database.dbCoinQueries
    private val coinPriceQueries = database.dbCoinPriceDataQueries
    private val portfolioQueries = database.dbPortfolioCoinQueries
    private val combinedTableQueries = database.dbCombinedTableQueries

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        coinQueries.searchCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .asFlow()
            .mapToList()

    override fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>> =
        combinedTableQueries.searchUnownedCoinsByName(query.toString(), CoinMappers.dbCoinMapper)
            .asFlow()
            .mapToList()

    override fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        coinQueries.searchCoinsByName(symbol, CoinMappers.dbCoinMapper)
            .asFlow()
            .mapToList()

    override fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        portfolioQueries.selectUnitsOwnedForSymbol(symbol)
            .asFlow()
            .mapToList()

    override fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>> =
        combinedTableQueries.getPortfolioData(CoinMappers.dbCoinWithPriceAndAmountMapper)
            .asFlow()
            .mapToList()

    @ExperimentalCoroutinesApi
    override fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean
    ): Flow<List<CoinPriceData>> =
        object : RxNetworkBoundResource<DbCoinPriceData, RemoteCoinPriceData, CoinPriceData>() {
            override suspend fun saveToDb(data: List<DbCoinPriceData>) {
                if (data.isNotEmpty()) {
                    val dbCoinPriceData = data[0]
                    coinPriceQueries.insert(
                        dbCoinPriceData.symbol,
                        dbCoinPriceData.price,
                        dbCoinPriceData.open24Hour,
                        dbCoinPriceData.high24Hour,
                        dbCoinPriceData.low24Hour
                    )
                } else {
                    coinPriceQueries.insertSymbol(symbol)
                }
            }

            override fun shouldFetch(data: List<DbCoinPriceData>): Boolean =
                data.isEmpty() || forceRefresh

            override fun mapToDbType(value: RemoteCoinPriceData): List<DbCoinPriceData> {
                if (value.rawData?.containsKey(symbol) == true) {
                    val rawData = value.rawData!![symbol] ?: return emptyList()
                    if (rawData.containsKey(Constants.MyCurrency)) {
                        val coinPriceRawData = rawData[Constants.MyCurrency] ?: return emptyList()
                        val coinPriceData = NetworkToDbMapper.mapSqlDelightCoinPriceData(coinPriceRawData)
                        return listOf(coinPriceData)
                    }
                }
                return emptyList()
            }

            override fun loadFromDb(): Flow<List<DbCoinPriceData>> =
                coinPriceQueries.selectBySymbol(symbol)
                    .asFlow()
                    .mapToList()

            override suspend fun loadFromNetwork(): RemoteCoinPriceData =
                service.getFullCoinPrice(symbol, Constants.MyCurrency)

            override fun mapToUiType(value: DbCoinPriceData): CoinPriceData = value.toUi()
        }.flow

    override fun getPortfolioCoinSymbols(): Flow<List<String>> =
        portfolioQueries.selectAllSymbols()
            .asFlow()
            .mapToList()

    override fun addTemporarySubscription(symbol: String, currency: String) {
        webSocket.addTemporarySubscription(symbol, currency)
    }

    override fun clearTemporarySubscriptions(currency: String) {
        webSocket.clearTemporarySubscriptions(currency)
    }

    override fun connectToLivePrices(symbols: Collection<String>, currency: String) {
        webSocket.connect { socket -> socket.setPortfolioSubscriptions(symbols, currency) }
    }

    override fun disconnectFromLivePrices() {
        webSocket.disconnect()
    }

    override fun priceUpdateReceived(): Flow<SymbolPricePair> =
        webSocket.priceUpdateReceived()

    override suspend fun forceRefreshCoinListAndSaveToDb() = withContext(Dispatchers.IO) {
        val serverCoinList = service.getCoinList()
        val baseImageUrl = serverCoinList.baseImageUrl
        serverCoinList.data.values.forEach { coinData ->
            coinQueries.insert(
                coinData.symbol,
                baseImageUrl + coinData.imageUrl,
                coinData.coinName,
                coinData.sortOrder.toLong()
            )
        }
    }

    override suspend fun refreshCoinListIfNeeded() = withContext(Dispatchers.IO) {
        val coinList = coinQueries.selectAll().executeAsList()
        if(coinList.isEmpty()) {
            forceRefreshCoinListAndSaveToDb()
        }
    }

    override suspend fun addPortfolioCoin(symbol: String, amountOwned: Double) =
        withContext(Dispatchers.IO) {
            portfolioQueries.insert(symbol, amountOwned)
        }

    override suspend fun removeCoinFromPortfolio(symbol: String) = withContext(Dispatchers.IO) {
        portfolioQueries.deleteBySymbol(symbol)
    }

    override suspend fun updatePriceForCoin(coinSymbol: String, price: Double) =
        withContext(Dispatchers.IO) {
            coinPriceQueries.updatePrice(price, coinSymbol)
        }

    override suspend fun getHistoricalDataForCoin(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean
    ): CoinHistory {
        val historicalData =
            when (timePeriod.timeUnit) {
                CoinHistoryUnits.MINUTE -> service.getHistoricalDataByMinute(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
                CoinHistoryUnits.HOUR -> service.getHistoricalDataByHour(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
                CoinHistoryUnits.DAY -> service.getHistoricalDataByDay(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
            }
        return CoinHistory(historicalData)
    }
}
