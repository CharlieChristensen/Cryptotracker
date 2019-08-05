package com.charliechristensen.cryptotracker.data.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.charliechristensen.cryptotracker.testUtil.CoinFactory
import com.charliechristensen.cryptotracker.testUtil.DataFactory
import com.charliechristensen.database.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for CoinPriceDao
 */
@RunWith(AndroidJUnit4::class)
class CoinPriceDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }


    @Test
    fun getPriceDataWhenNotExist() {
        val symbol = DataFactory.randomSymbol()
        database.coinPriceDao().getPrice(symbol)
            .test()
            .assertValue { it.isEmpty() }
    }

    @Test
    fun insertAndGetPriceData() {
        val coinPriceData = CoinFactory.makeCoinPriceData()
        database.coinPriceDao().setPrice(coinPriceData)
        database.coinPriceDao().getPrice(coinPriceData.symbol)
            .test()
            .assertValue {
                it.size == 1 &&
                        it[0].symbol == coinPriceData.symbol &&
                        it[0].price == coinPriceData.price &&
                        it[0].high24Hour == coinPriceData.high24Hour &&
                        it[0].low24Hour == coinPriceData.low24Hour &&
                        it[0].open24Hour == coinPriceData.open24Hour
            }
    }

    @Test
    fun updatePriceNotExist() {
        val symbol = DataFactory.randomSymbol()
        val price = DataFactory.randomDouble()
        database.coinPriceDao().updatePrice(symbol, price)
        database.coinPriceDao().getPrice(symbol)
            .test()
            .assertValue { it.isEmpty() }
    }

    @Test
    fun updatePrice() {
        val coinPriceData = CoinFactory.makeCoinPriceData()
        database.coinPriceDao().setPrice(coinPriceData)
        val newPrice = coinPriceData.price + DataFactory.randomDouble()
        database.coinPriceDao().updatePrice(coinPriceData.symbol, newPrice)
        database.coinPriceDao().getPrice(coinPriceData.symbol)
            .test()
            .assertValue {
                it.size == 1 && it[0].price == newPrice
            }
    }


}