package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.RxNetworkBoundResource
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryUnits.*
import com.charliechristensen.cryptotracker.data.database.daos.CoinDao
import com.charliechristensen.cryptotracker.data.database.daos.CoinPriceDao
import com.charliechristensen.cryptotracker.data.database.daos.CombinedTableDao
import com.charliechristensen.cryptotracker.data.database.daos.PortfolioCoinDao
import com.charliechristensen.cryptotracker.data.mappers.NetworkToDbMapper
import com.charliechristensen.cryptotracker.data.models.graph.CoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.models.database.DbCoin
import com.charliechristensen.cryptotracker.data.models.database.DbCoinPriceData
import com.charliechristensen.cryptotracker.data.models.database.DbCoinWithPriceAndAmount
import com.charliechristensen.cryptotracker.data.models.database.DbPortfolioCoin
import com.charliechristensen.cryptotracker.data.models.network.ServerCoinPriceData
import com.charliechristensen.cryptotracker.data.webservice.CryptoService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Repository
 */
class Repository(
    private val service: CryptoService,
    private val coinDao: CoinDao,
    private val coinPriceDao: CoinPriceDao,
    private val portfolioCoinDao: PortfolioCoinDao,
    private val combinedTableDao: CombinedTableDao
) {

    fun searchCoinsWithQuery(query: CharSequence): Observable<List<DbCoin>> =
        coinDao.searchCoinsByName(query.toString())

    fun searchUnownedCoinWithQuery(query: CharSequence): Observable<List<DbCoin>> =
        combinedTableDao.searchUnownedCoinsByName(query.toString())

    fun getCoinDetails(symbol: String): Observable<List<DbCoin>> = coinDao.getCoin(symbol)

    fun getUnitsOwnedForSymbol(symbol: String): Observable<List<Double>> =
        portfolioCoinDao.getUnitsOwnedForSymbol(symbol)

    fun getPortfolioData(): Observable<List<DbCoinWithPriceAndAmount>> =
        combinedTableDao.getPortfolioData()

    fun getCoinPriceData(
        symbol: String,
        forceRefresh: Boolean = true
    ): Observable<List<DbCoinPriceData>> =
        object : RxNetworkBoundResource<DbCoinPriceData, ServerCoinPriceData>() {
            override fun saveToDb(data: List<DbCoinPriceData>) {
                if (data.isNotEmpty()) {
                    coinPriceDao.setPrice(data[0])
                } else {
                    coinPriceDao.setPrice(
                        DbCoinPriceData(
                            symbol
                        )
                    ) //Coin data does not exist
                }
            }

            override fun shouldFetch(data: List<DbCoinPriceData>): Boolean =
                data.isEmpty() || forceRefresh

            override fun mapToDbType(value: ServerCoinPriceData): List<DbCoinPriceData> {
                if (value.rawData?.containsKey(symbol) == true) {
                    val rawData = value.rawData?.get(symbol) ?: return emptyList()
                    if (rawData.containsKey(Constants.MyCurrency)) {
                        val coinPriceRawData = rawData[Constants.MyCurrency] ?: return emptyList()
                        val coinPriceData = NetworkToDbMapper.mapCoinPriceData(coinPriceRawData)
                        return listOf(coinPriceData)
                    }
                }
                return emptyList()
            }

            override fun loadFromDb(): Observable<List<DbCoinPriceData>> =
                coinPriceDao.getPrice(symbol)

            override fun loadFromNetwork(): Single<ServerCoinPriceData> =
                service.getFullCoinPrice(symbol, Constants.MyCurrency)

        }.observable

    fun forceRefreshCoinListAndSaveToDb(): Completable =
        service.getCoinList()
            .map { serverCoinList ->
                serverCoinList.data
                    .asSequence()
                    .map { NetworkToDbMapper.mapCoin(it.value, serverCoinList.baseImageUrl) }
                    .toList()
            }
            .doOnSuccess { coinDao.insertCoins(it) }
            .toCompletable()

    fun refreshCoinListIfNeeded(): Completable =
        coinDao.getAllCoins()
            .map { it.isEmpty() }
            .flatMapCompletable {
                if (it) {
                    forceRefreshCoinListAndSaveToDb()
                } else {
                    Completable.complete()
                }
            }

    fun getPortfolioCoinSymbols(): Observable<List<String>> =
        portfolioCoinDao.getPortfolioCoinSymbols()

    fun addPortfolioCoin(symbol: String, amountOwned: Double): Completable =
        Completable.fromAction {
            portfolioCoinDao.addCoinToPortfolio(DbPortfolioCoin(symbol, amountOwned))
        }

    fun removeCoinFromPortfolio(symbol: String): Completable =
        Completable.fromAction { portfolioCoinDao.removeCoinFromPortfolio(symbol) }

    //TODO Save to DB
    fun getHistoricalDataForCoin(
        symbol: String,
        timePeriod: CoinHistoryTimePeriod,
        forceRefresh: Boolean = true
    ): Single<CoinHistory> =
        when (timePeriod.timeUnit) {
            MINUTE -> service.getHistoricalDataByMinute(
                symbol,
                Constants.MyCurrency,
                timePeriod.limit
            )
            HOUR -> service.getHistoricalDataByHour(symbol, Constants.MyCurrency, timePeriod.limit)
            DAY -> service.getHistoricalDataByDay(symbol, Constants.MyCurrency, timePeriod.limit)
        }.map { CoinHistory(it) }

    fun updatePriceForCoin(coinSymbol: String, price: Double) {
        coinPriceDao.updatePrice(coinSymbol, price)
    }
}

//    fun getAllCoins(forceRefresh: Boolean = false): Observable<List<DbCoin>> =
//        object : RxNetworkBoundResource<DbCoin, ServerCoinList>() {
//            override fun saveToDb(data: List<DbCoin>) {
//                coinDao.insertCoins(data)
//            }
//
//            override fun shouldFetch(data: List<DbCoin>): Boolean =
//                data.isEmpty() || forceRefresh
//
//            override fun mapToDbType(value: ServerCoinList): List<DbCoin> =
//                value.data.asSequence().map { DbCoin(it.value, value.baseImageUrl) }.toList()
//
//            override fun loadFromDb(): Observable<List<DbCoin>> =
//                coinDao.getAllCoins()
//
//            override fun loadFromNetwork(): Single<ServerCoinList> =
//                service.getCoinList()
//
//        }.observable