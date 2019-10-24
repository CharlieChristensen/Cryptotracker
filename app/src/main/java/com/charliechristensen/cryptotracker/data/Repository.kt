package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.RxNetworkBoundResource
import com.charliechristensen.cryptotracker.data.database.daos.CoinDao
import com.charliechristensen.cryptotracker.data.database.daos.CoinPriceDao
import com.charliechristensen.cryptotracker.data.database.daos.CombinedTableDao
import com.charliechristensen.cryptotracker.data.database.daos.PortfolioCoinDao
import com.charliechristensen.cryptotracker.data.mappers.NetworkToDbMapper
import com.charliechristensen.cryptotracker.data.mappers.toUi
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.database.DbCoinPriceData
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.database.DbPortfolioCoin
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.network.ServerCoinPriceData
import com.charliechristensen.cryptotracker.data.models.ui.Coin
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.*
import com.charliechristensen.cryptotracker.data.webservice.CryptoService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository
 */
@ExperimentalCoroutinesApi
class Repository(
    private val service: CryptoService,
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
    private val portfolioCoinDao: PortfolioCoinDao,
    private val combinedTableDao: CombinedTableDao
) {

    fun searchCoinsWithQuery(query: CharSequence): Flow<List<DbCoin>> =
        coinDao.searchCoinsByName(query.toString())

    fun searchUnownedCoinWithQuery(query: CharSequence): Flow<List<DbCoin>> =
        combinedTableDao.searchUnownedCoinsByName(query.toString())

    fun getCoinDetails(symbol: String): Flow<List<Coin>> =
        coinDao.getCoin(symbol)
            .map { dbCoinList ->
                dbCoinList.map { coin -> coin.toUi() }
            }

    fun getUnitsOwnedForSymbol(symbol: String): Flow<List<Double>> =
        portfolioCoinDao.getUnitsOwnedForSymbol(symbol)

    fun getPortfolioData(): Flow<List<DbCoinWithPriceAndAmount>> =
        combinedTableDao.getPortfolioData()

    fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean = true
    ): Flow<List<DbCoinPriceData>> =
        object : RxNetworkBoundResource<DbCoinPriceData, ServerCoinPriceData>() {
            override suspend fun saveToDb(data: List<DbCoinPriceData>) {
                if (data.isNotEmpty()) {
                    coinPriceDao.setPrice(data[0])
                } else {
                    coinPriceDao.setPrice(
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
                    val rawData = value.rawData[symbol] ?: return emptyList()
                    if (rawData.containsKey(Constants.MyCurrency)) {
                        val coinPriceRawData = rawData[Constants.MyCurrency] ?: return emptyList()
                        val coinPriceData = NetworkToDbMapper.mapCoinPriceData(coinPriceRawData)
                        return listOf(coinPriceData)
                    }
                }
                return emptyList()
            }

            override fun loadFromDbs(): Flow<List<DbCoinPriceData>> =
                coinPriceDao.getPrice(symbol)

            override suspend fun loadFromNetworks(): ServerCoinPriceData =
                service.getFullCoinPrice(symbol, Constants.MyCurrency)

        }.flow

    suspend fun forceRefreshCoinListAndSaveToDb() {
        val serverCoinList = service.getCoinList()
        val coinList = serverCoinList.data
            .map { NetworkToDbMapper.mapCoin(it.value, serverCoinList.baseImageUrl) }
        coinDao.insertCoins(coinList)
    }

    suspend fun refreshCoinListIfNeeded() {
        val coinList = coinDao.getAllCoins().first()
        if (coinList.isEmpty()) {
            forceRefreshCoinListAndSaveToDb()
        }
    }

    fun getPortfolioCoinSymbols(): Flow<List<String>> =
        portfolioCoinDao.getPortfolioCoinSymbols()

    suspend fun addPortfolioCoin(symbol: String, amountOwned: Double) =
        portfolioCoinDao.addCoinToPortfolios(DbPortfolioCoin(symbol, amountOwned))

    suspend fun removeCoinFromPortfolio(symbol: String) =
        portfolioCoinDao.removeCoinFromPortfolios(symbol)

    //TODO Save to DB
    suspend fun getHistoricalDataForCoin(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean = true
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

    suspend fun updatePriceForCoin(coinSymbol: String, price: Double) =
        coinPriceDao.updatePrice(coinSymbol, price)

}