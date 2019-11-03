package com.charliechristensen.cryptotracker.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Exposes observable while deciding when to call network and cache
 */
@ExperimentalCoroutinesApi
abstract class RxNetworkBoundResource<DbType, RemoteType> {

    private var isFirstElement: Boolean = true

    val flow : Flow<List<DbType>> by lazy {
        loadFromDb()
            .flatMapLatest { dbList ->
                if (isFirstElement && shouldFetch(dbList)) {
                    isFirstElement = false
                    flow {
                        emit(dbList)
                        emit(loadFromNetworkAndMap())
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    protected abstract suspend fun saveToDb(data: List<DbType>)

    protected abstract fun shouldFetch(data: List<DbType>): Boolean

    protected abstract fun loadFromDb(): Flow<List<DbType>>

    protected abstract suspend fun loadFromNetwork(): RemoteType

    private suspend fun loadFromNetworkAndMap(): List<DbType> {
        val networkResponse = loadFromNetwork()
        val mappedResponse = mapToDbType(networkResponse)
        saveToDb(mappedResponse)
        return mappedResponse
    }

    protected abstract fun mapToDbType(value: RemoteType): List<DbType>

}
