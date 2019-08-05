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
 * Tests for CoinDao
 */
@RunWith(AndroidJUnit4::class)
class CoinDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getCoin_WhenNotExist() {
        val symbol = DataFactory.randomSymbol()
        database.coinDao().getCoin(symbol)
                .test()
                .assertValue { it.isEmpty() }
    }

    @Test
    fun getAllCoins_WhenNotExist() {
        database.coinDao().getAllCoins()
                .test()
                .assertValue { it.isEmpty() }
    }

    @Test
    fun insertAndGetCoin() {
        val coin = CoinFactory.makeDbCoin()
        database.coinDao().insertCoin(coin)

        database.coinDao()
                .getCoin(coin.symbol)
                .test()
                .assertValue {
                    it.isNotEmpty() &&
                            it[0].symbol == coin.symbol &&
                            it[0].imageUrl == coin.imageUrl &&
                            it[0].coinName == coin.coinName &&
                            it[0].sortOrder == coin.sortOrder
                }
    }

    @Test
    fun insertCoinList_GetAll() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)

        database.coinDao()
                .getAllCoins()
                .test()
                .assertValue { coinsList.size == it.size }
    }

    @Test
    fun updateAndGetCoin() {
        val coin = CoinFactory.makeDbCoin()
        database.coinDao().insertCoin(coin)

        val updatedCoin = CoinFactory.makeDbCoin(coin.symbol)
        database.coinDao().insertCoin(updatedCoin)

        database.coinDao()
                .getCoin(coin.symbol)
                .test()
                .assertValue {
                    it.size == 1 &&
                            it[0].symbol == updatedCoin.symbol &&
                            it[0].imageUrl == updatedCoin.imageUrl &&
                            it[0].coinName == updatedCoin.coinName &&
                            it[0].sortOrder == updatedCoin.sortOrder
                }
    }

    @Test
    fun updateCoinList_GetAllCoins() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)

        val updatedCoinList = coinsList.map {
            CoinFactory.makeDbCoin(it.symbol)
        }
        database.coinDao().insertCoins(updatedCoinList)

        database.coinDao()
                .getAllCoins()
                .test()
                .assertValue { coinsList.size == it.size }
    }

    @Test
    fun searchCoinsByName_Empty() {
        val randomCoinName = CoinFactory.makeDbCoin().coinName
        database.coinDao()
                .searchCoinsByName(randomCoinName)
                .test()
                .assertValue { it.isEmpty() }
    }

    @Test
    fun searchCoinsByName_Success() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)
        val coin = coinsList[0]

        database.coinDao()
                .searchCoinsByName(coin.coinName)
                .test()
                .assertValue {
                    it.isNotEmpty() &&
                            it.contains(coin)
                }
    }

    @Test
    fun searchCoinsByName_BlankReturnsAll() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)
        database.coinDao()
                .searchCoinsByName("")
                .test()
                .assertValue { it.size == coinsList.size }
    }

    @Test
    fun searchCoinsByName_TestSort() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)
        database.coinDao()
            .searchCoinsByName("")
            .test()
            .assertValue {
                it.windowed(2) //Selects each neighboring elements
                    .none { (a, b) -> a.sortOrder > b.sortOrder }
            }
    }


    @Test
    fun searchCoinsByName_InvalidSearch() {
        val coinsList = CoinFactory.makeDbCoinList(5)
        database.coinDao().insertCoins(coinsList)
        val randomCoinName = DataFactory.randomDouble().toString()
        database.coinDao()
                .searchCoinsByName(randomCoinName)
                .test()
                .assertValue { it.isEmpty() }
    }

}