package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.RxNetworkBoundResource
import com.charliechristensen.cryptotracker.common.extensions.mapItems
import com.charliechristensen.cryptotracker.data.mappers.NetworkToDbMapper
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.DAY
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.HOUR
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.MINUTE
import com.charliechristensen.cryptotracker.data.models.ui.CoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.CoinWithPriceAndAmount
import com.charliechristensen.database.DatabaseApi
import com.charliechristensen.database.models.DbCoinPriceData
import com.charliechristensen.database.models.DbPortfolioCoin
import com.charliechristensen.remote.models.RemoteCoinPriceData
import com.charliechristensen.remote.models.SymbolPricePair
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository
 */
class RepositoryImpl @Inject constructor(
    private val service: CryptoService,
    private val database: DatabaseApi,
    private val webSocket: WebSocketService
) : Repository {

    override fun searchCoinsWithQuery(query: CharSequence): Flow<List<Coin>> =
        database.searchCoinsByName(query.toString())
            .mapItems { coin -> coin.toUi() }

    override fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<Coin>> =
        database.searchUnownedCoinsByName(query.toString())
            .mapItems { coin -> coin.toUi() }

    override fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        database.getCoin(symbol)
            .mapItems { coin -> coin.toUi() }

    override fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        database.getUnitsOwnedForSymbol(symbol)

    override fun getPortfolioData(): Flow<List<CoinWithPriceAndAmount>> =
        database.getPortfolioData()
            .mapItems { it.toUi() }

    @ExperimentalCoroutinesApi
    override fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean
    ): Flow<List<CoinPriceData>> =
        object : RxNetworkBoundResource<DbCoinPriceData, RemoteCoinPriceData, CoinPriceData>() {
            override suspend fun saveToDb(data: List<DbCoinPriceData>) {
                if (data.isNotEmpty()) {
                    database.setPrice(data[0])
                } else {
                    database.setPrice(DbCoinPriceData(symbol))
                }
            }

            override fun shouldFetch(data: List<DbCoinPriceData>): Boolean =
                data.isEmpty() || forceRefresh

            override fun mapToDbType(value: RemoteCoinPriceData): List<DbCoinPriceData> {
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
                database.getPrice(symbol)

            override suspend fun loadFromNetwork(): RemoteCoinPriceData =
                service.getFullCoinPrice(symbol, Constants.MyCurrency)

            override fun mapToUiType(value: DbCoinPriceData): CoinPriceData = value.toUi()
        }.flow

    override suspend fun forceRefreshCoinListAndSaveToDb() {
        val serverCoinList = service.getCoinList()
        val coinList = serverCoinList.data
            .map { NetworkToDbMapper.mapCoin(it.value, serverCoinList.baseImageUrl) }
        database.insertCoins(coinList)
    }

    override suspend fun refreshCoinListIfNeeded() {
        val coinList = database.getAllCoins().first()
        if (coinList.isEmpty()) {
            forceRefreshCoinListAndSaveToDb()
        }
    }

    override fun getPortfolioCoinSymbols(): Flow<List<String>> =
        database.getPortfolioCoinSymbols()

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

    override suspend fun addPortfolioCoin(symbol: String, amountOwned: Double) =
        database.addCoinToPortfolios(DbPortfolioCoin(symbol, amountOwned))

    override suspend fun removeCoinFromPortfolio(symbol: String) =
        database.removeCoinFromPortfolios(symbol)

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
        database.updatePrice(coinSymbol, price)
}
