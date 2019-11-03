//package com.charliechristensen.cryptotracker.data.database.daos
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.room.Room
//import androidx.test.InstrumentationRegistry
//import androidx.test.runner.AndroidJUnit4
//import com.charliechristensen.cryptotracker.data.database.AppDatabase
//import com.charliechristensen.cryptotracker.data.database.testUtils.CoinFactory
//import com.charliechristensen.cryptotracker.data.database.testUtils.DataFactory
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
///**
// * Tests for CombinedTableDao
// */
//@RunWith(AndroidJUnit4::class)
//class CombinedTableDaoTest {
//
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var database: AppDatabase
//
//    @Before
//    fun initDb() {
//        database = Room.inMemoryDatabaseBuilder(
//            InstrumentationRegistry.getContext(),
//            AppDatabase::class.java
//        )
//            .allowMainThreadQueries()
//            .build()
//    }
//
//    @After
//    fun closeDb() {
//        database.close()
//    }
//
//    @Test
//    fun searchUnownedCoins_SearchAll_ReturnsUnownedCoins() {
//        val coin1 = CoinFactory.makeDbCoin()
//        val coin2 = CoinFactory.makeDbCoin()
//        database.coinDao().insertCoins(listOf(coin1, coin2))
//        val portfolioCoin = CoinFactory.makePortfolioCoin(coin2.symbol)
//        database.portfolioCoinDao().addCoinToPortfolio(portfolioCoin)
//
//        database.combinedTableDao().searchUnownedCoinsByName("")
//            .test()
//            .assertValue {
//                it.size == 1 && it[0].symbol == coin1.symbol
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_SearchOwnedCoin_ReturnsEmpty() {
//        val coin1 = CoinFactory.makeDbCoin()
//        val coin2 = CoinFactory.makeDbCoin()
//        database.coinDao().insertCoins(listOf(coin1, coin2))
//        val portfolioCoin = CoinFactory.makePortfolioCoin(coin2.symbol)
//        database.portfolioCoinDao().addCoinToPortfolio(portfolioCoin)
//
//        database.combinedTableDao().searchUnownedCoinsByName(portfolioCoin.symbol)
//            .test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_NoOwnedCoins_ReturnsAll() {
//        val coinList = CoinFactory.makeDbCoinList(5)
//        database.coinDao().insertCoins(coinList)
//
//        database.combinedTableDao().searchUnownedCoinsByName("")
//            .test()
//            .assertValue {
//                it.size == coinList.size
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_TestSort() {
//        val coinList = CoinFactory.makeDbCoinList(5)
//        database.coinDao().insertCoins(coinList)
//        database.combinedTableDao().searchUnownedCoinsByName("")
//            .test()
//            .assertValue {
//                it.windowed(2) //Selects each neighboring elements
//                    .none { (a, b) -> a.sortOrder > b.sortOrder }
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_NoCoins_ReturnsEmpty() {
//        database.combinedTableDao().searchUnownedCoinsByName("")
//            .test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_NoCoins_But_HasOwnedCoin_ReturnsEmpty() {
//        val portfolioCoin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(portfolioCoin)
//        database.combinedTableDao().searchUnownedCoinsByName("")
//            .test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//    @Test
//    fun searchUnownedCoins_InvalidSearch_ReturnsEmpty() {
//        val coin1 = CoinFactory.makeDbCoin()
//        val coin2 = CoinFactory.makeDbCoin()
//        database.coinDao().insertCoins(listOf(coin1, coin2))
//        val portfolioCoin = CoinFactory.makePortfolioCoin(coin2.symbol)
//        database.portfolioCoinDao().addCoinToPortfolio(portfolioCoin)
//        database.combinedTableDao()
//            .searchUnownedCoinsByName(DataFactory.randomDouble().toString())
//            .test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//
//    @Test
//    fun getPortfolioDataForSymbols_CoinInPortfolio_ReturnsCombinedData() {
//        val coin = CoinFactory.makeDbCoin()
//        val portfolioCoin = CoinFactory.makePortfolioCoin(coin.symbol)
//        val coinPriceData = CoinFactory.makeCoinPriceData(coin.symbol)
//
//        database.coinDao().insertCoin(coin)
//        database.portfolioCoinDao().addCoinToPortfolio(portfolioCoin)
//        database.coinPriceDao().setPrice(coinPriceData)
//
//        database.combinedTableDao().getPortfolioData()
//            .test()
//            .assertValue {
//                it.size == 1 &&
//                        it[0].symbol == coin.symbol &&
//                        it[0].imageUrl == coin.imageUrl &&
//                        it[0].amountOwned == portfolioCoin.amountOwned &&
//                        it[0].open24Hour == coinPriceData.open24Hour &&
//                        it[0].price == coinPriceData.price
//            }
//    }
//
//    @Test
//    fun getPortfolioDataForSymbols_EmptyPortfolio_ReturnsEmpty() {
//        val coin = CoinFactory.makeDbCoin()
//        val coinPriceData = CoinFactory.makeCoinPriceData(coin.symbol)
//
//        database.coinDao().insertCoin(coin)
//        database.coinPriceDao().setPrice(coinPriceData)
//
//        database.combinedTableDao().getPortfolioData()
//            .test()
//            .assertValue { it.isEmpty() }
//    }
//
//
//}
