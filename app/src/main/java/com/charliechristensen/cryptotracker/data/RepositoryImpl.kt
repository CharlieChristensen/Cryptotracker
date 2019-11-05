package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.RxNetworkBoundResource
import com.charliechristensen.cryptotracker.common.extensions.mapItems
import com.charliechristensen.cryptotracker.data.mappers.NetworkToDbMapper
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.*
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.database.DatabaseApi
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.database.models.DbPortfolioCoin
import com.charliechristensen.remote.models.ServerCoinPriceData
import com.charliechristensen.remote.models.SymbolPricePair
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository
 */
@FlowPreview
@ExperimentalCoroutinesApi
internal class RepositoryImpl constructor(
    private val service: CryptoService,
    private val databaseApi: DatabaseApi,
    private val webSocket: WebSocketService
) : Repository {

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        databaseApi.searchCoinsByName(query.toString())
            .mapItems { coin -> coin.toUi() }

    override fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>> =
        databaseApi.searchUnownedCoinsByName(query.toString())
            .mapItems { coin -> coin.toUi() }

    override fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        databaseApi.getCoin(symbol)
            .map { dbCoinList ->
                dbCoinList.map { coin -> coin.toUi() }
            }

    override fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        databaseApi.getUnitsOwnedForSymbol(symbol)

    override fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>> =
        databaseApi.getPortfolioData()
            .mapItems { it.toUi() }

    override fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean
    ): Flow<List<CoinPriceData>> =
        object : RxNetworkBoundResource<DbCoinPriceData, ServerCoinPriceData>() {
            override suspend fun saveToDb(data: List<DbCoinPriceData>) {
                if (data.isNotEmpty()) {
                    databaseApi.setPrice(data[0])
                } else {
                    databaseApi.setPrice(
                        DbCoinPriceData(
                            symbol
                        )
                    )
                }
            }

            override fun shouldFetch(data: List<DbCoinPriceData>): Boolean =
                data.isEmpty() || forceRefresh

            override fun mapToDbType(value: ServerCoinPriceData): List<DbCoinPriceData> {
                if (value.rawData?.containsKey(symbol) == true) {
                    val rawData = value.rawData!![symbol] ?: return emptyList()
                    if (rawData.containsKey(Constants.MyCurrency)) {
                        val coinPriceRawData = rawData[Constants.MyCurrency] ?: return emptyList()
                        val coinPriceData = NetworkToDbMapper.mapCoinPriceData(coinPriceRawData)
                        return listOf(coinPriceData)
                    }
                }
                return emptyList()
            }

            override fun loadFromDb(): Flow<List<DbCoinPriceData>> =
                databaseApi.getPrice(symbol)

            override suspend fun loadFromNetwork(): ServerCoinPriceData =
                service.getFullCoinPrice(symbol, Constants.MyCurrency)

        }.flow.mapItems { it.toUi() }

    override suspend fun forceRefreshCoinListAndSaveToDb() {
        val serverCoinList = service.getCoinList()
        val coinList = serverCoinList.data
            .map { NetworkToDbMapper.mapCoin(it.value, serverCoinList.baseImageUrl) }
        databaseApi.insertCoins(coinList)
    }

    override suspend fun refreshCoinListIfNeeded() {
        val coinList = databaseApi.getAllCoins().first()
        if (coinList.isEmpty()) {
            forceRefreshCoinListAndSaveToDb()
        }
    }

    override fun getPortfolioCoinSymbols(): Flow<List<String>> =
        databaseApi.getPortfolioCoinSymbols()

    override fun addTemporarySubscription(symbol: String, currency: String) {
        webSocket.addTemporarySubscription(symbol, currency)
    }

    override fun clearTemporarySubscriptions(currency: String) {
        webSocket.clearTemporarySubscriptions(currency)
    }

    override fun connectToLivePrices(symbols: Collection<String>, currency: String) {
        webSocket.connect { socket ->
            socket.setPortfolioSubscriptions(symbols, currency)
        }
    }

    override fun disconnectFromLivePrices() {
        webSocket.disconnect()
    }

    override fun priceUpdateReceived(): Flow<SymbolPricePair> =
        webSocket.priceUpdateReceived()

    override suspend fun addPortfolioCoin(symbol: String, amountOwned: Double) =
        databaseApi.addCoinToPortfolios(DbPortfolioCoin(symbol, amountOwned))

    override suspend fun removeCoinFromPortfolio(symbol: String) =
        databaseApi.removeCoinFromPortfolios(symbol)

    //TODO Save to DB
    override suspend fun getHistoricalDataForCoin(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean
    ): CoinHistory {
        val historicalData =
            when (timePeriod.timeUnit) {
                MINUTE -> service.getHistoricalDataByMinute(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
                HOUR -> service.getHistoricalDataByHour(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
                DAY -> service.getHistoricalDataByDay(
                    symbol,
                    Constants.MyCurrency,
                    timePeriod.limit
                )
            }
        return CoinHistory(historicalData)
    }

    override suspend fun updatePriceForCoin(coinSymbol: String, price: Double) =
        databaseApi.updatePrice(coinSymbol, price)

}
