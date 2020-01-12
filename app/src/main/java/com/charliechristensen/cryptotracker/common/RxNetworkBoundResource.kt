package com.charliechristensen.cryptotracker.common

import com.charliechristensen.cryptotracker.common.extensions.mapItems
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * Exposes Flow while deciding when to call network and cache
 */
abstract class RxNetworkBoundResource<DbType, RemoteType, UiType> {

    private var isFirstElement: Boolean = true

    @ExperimentalCoroutinesApi
    val flow: Flow<List<UiType>> by lazy {
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
            .mapItems { mapToUiType(it) }
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

    protected abstract fun mapToUiType(value: DbType): UiType
}
