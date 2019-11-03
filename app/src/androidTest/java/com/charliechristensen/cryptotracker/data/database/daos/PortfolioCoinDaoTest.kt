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
// * Tests for PortfolioCoinDao
// */
//@RunWith(AndroidJUnit4::class)
//class PortfolioCoinDaoTest {
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
//    fun getPortfolioCoinWhenNotExist() {
//        val symbol = DataFactory.randomSymbol()
//        database.portfolioCoinDao().getCoinFromPortfolio(symbol)
//            .test()
//            .assertValue { it.isEmpty() }
//    }
//
//    @Test
//    fun insertAndGetPortfolioCoin() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(coin)
//        database.portfolioCoinDao()
//            .getCoinFromPortfolio(coin.symbol)
//            .test()
//            .assertValue {
//                it.size == 1 &&
//                        it[0].symbol == coin.symbol &&
//                        it[0].amountOwned == coin.amountOwned
//            }
//    }
//
//    @Test
//    fun updateAndGetPortfolioCoin() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(coin)
//
//        val updatedCoin = CoinFactory.makePortfolioCoin(coin.symbol)
//        database.portfolioCoinDao().addCoinToPortfolio(updatedCoin)
//
//        database.portfolioCoinDao()
//            .getCoinFromPortfolio(coin.symbol)
//            .test()
//            .assertValue {
//                it.size == 1 &&
//                        it[0].symbol == updatedCoin.symbol &&
//                        it[0].amountOwned == updatedCoin.amountOwned
//            }
//    }
//
//    @Test
//    fun deleteAndGetPortfolioCoin() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(coin)
//        database.portfolioCoinDao().removeCoinFromPortfolio(coin.symbol)
//        database.portfolioCoinDao().getCoinFromPortfolio(coin.symbol)
//            .test()
//            .assertValue { it.isEmpty() }
//    }
//
//    @Test
//    fun getPortfolioSymbolsNotExist() {
//        database.portfolioCoinDao().getPortfolioCoinSymbols()
//            .test()
//            .assertValue { it.isEmpty() }
//    }
//
//    @Test
//    fun getPortfolioSymbols() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(coin)
//        database.portfolioCoinDao().getPortfolioCoinSymbols()
//            .test()
//            .assertValue {
//                it.isNotEmpty() && it[0] == coin.symbol
//            }
//    }
//
//    @Test
//    fun getPortfolioSymbolsList() {
//        val coinList = CoinFactory.makePortfolioCoinList(5)
//        coinList.forEach {
//            database.portfolioCoinDao().addCoinToPortfolio(it)
//        }
//        database.portfolioCoinDao().getPortfolioCoinSymbols()
//            .test()
//            .assertValue { it.size == coinList.size }
//    }
//
//    @Test
//    fun getUnitsOwnedForSymbolNotExist() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().getUnitsOwnedForSymbol(coin.symbol)
//            .test()
//            .assertValue {
//                it.isEmpty()
//            }
//    }
//
//    @Test
//    fun getUnitsOwnedForSymbol() {
//        val coin = CoinFactory.makePortfolioCoin()
//        database.portfolioCoinDao().addCoinToPortfolio(coin)
//        database.portfolioCoinDao().getUnitsOwnedForSymbol(coin.symbol)
//            .test()
//            .assertValue {
//                it.isNotEmpty() && it[0] == coin.amountOwned
//            }
//    }
//}
