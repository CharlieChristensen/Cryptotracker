package com.charliechristensen.cryptotracker.data

import com.charliechristensen.cryptotracker.data.database.daos.CoinDao
import com.charliechristensen.cryptotracker.data.database.daos.CombinedTableDao
import com.charliechristensen.cryptotracker.data.database.daos.PortfolioCoinDao
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * Created by Chuck on 1/17/2018.
 */
class RepositoryTest {

    private lateinit var repository: Repository
    private lateinit var coinDao: CoinDao
    private lateinit var portfolioCoinDao: PortfolioCoinDao
    private lateinit var combinedTableDao: CombinedTableDao

    @Before
    fun setup(){
        coinDao = mock {
            on { searchCoinsByName(any()) } doReturn Observable.just(mock())
            on { getCoin(any()) } doReturn Observable.just(mock())
        }
        portfolioCoinDao = mock {
            on { getUnitsOwnedForSymbol(any()) } doReturn Observable.just(mock())
        }
        combinedTableDao = mock {
            on { searchUnownedCoinsByName(any()) } doReturn Observable.just(mock())
            on { getPortfolioData() } doReturn Observable.just(mock())
        }
        repository = Repository(mock(), coinDao, mock(), portfolioCoinDao, combinedTableDao)
    }

    @Test
    fun searchCoinsWithQuery_MethodIsCalledAndParameterPassed() {
        val symbol = DataFactory.randomSymbol()
        repository.searchCoinsWithQuery(symbol)
            .test()
            .assertValueCount(1)
        verify(coinDao).searchCoinsByName(any())
    }

    @Test
    fun searchUnownedCoinWithQuery_MethodIsCalledAndParameterPassed() {
        val symbol = DataFactory.randomSymbol()
        repository.searchUnownedCoinWithQuery(symbol)
            .test()
            .assertValueCount(1)
        verify(combinedTableDao).searchUnownedCoinsByName(any())
    }

    @Test
    fun getCoinDetails() {
        val symbol = DataFactory.randomSymbol()
        repository.getCoinDetails(symbol)
            .test()
            .assertValueCount(1)
        verify(coinDao).getCoin(any())
    }

    @Test
    fun getUnitsOwnedForSymbol() {
        val symbol = DataFactory.randomSymbol()
        repository.getUnitsOwnedForSymbol(symbol)
            .test()
            .assertValueCount(1)
        verify(portfolioCoinDao).getUnitsOwnedForSymbol(any())
    }

    @Test
    fun getPortfolioDataForSymbols() {
        repository.getPortfolioData()
            .test()
            .assertValueCount(1)
        verify(combinedTableDao).getPortfolioData()

    }

    @Test
    fun getCoinPriceData() {

    }

    @Test
    fun forceRefreshCoinListAndSaveToDb() {

    }

    @Test
    fun refreshCoinListIfNeeded() {

    }

    @Test
    fun getPortfolioCoinSymbols() {
    }

    @Test
    fun addPortfolioCoin() {
    }

    @Test
    fun removeCoinFromPortfolio() {
    }

    @Test
    fun getHistoricalDataForCoin() {
    }

    @Test
    fun updatePriceForCoin() {
    }

}