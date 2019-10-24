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

//    val observable: Observable<List<DbType>> by lazy {
//        loadFromDb()
//            .switchMap {
//                if (isFirstElement && shouldFetch(it)) {
//                    isFirstElement = false
//                    Observable.concat(Observable.just(it), loadFromNetworkAndMap())
//                } else {
//                    Observable.just(it)
//                }
//            }
//            .distinctUntilChanged()
//    }

    val flow : Flow<List<DbType>> by lazy {
        loadFromDbs()
            .flatMapLatest { dbList ->
                if (isFirstElement && shouldFetch(dbList)) {
                    isFirstElement = false
                    flow {
                        emit(dbList)
                        emit(loadFromNetworkAndMaps())
                    }
                } else {
                    flowOf(dbList)
                }
            }
    }

    protected abstract suspend fun saveToDb(data: List<DbType>)

    protected abstract fun shouldFetch(data: List<DbType>): Boolean

//    protected abstract fun loadFromDb(): Observable<List<DbType>>

//    protected abstract fun loadFromNetwork(): Single<RemoteType>

    protected abstract fun loadFromDbs(): Flow<List<DbType>>

    protected abstract suspend fun loadFromNetworks(): RemoteType

//    private fun loadFromNetworkAndMap(): Observable<List<DbType>> =
//        loadFromNetwork()
//            .map { mapToDbType(it) }
//            .doOnSuccess { saveToDb(it) }
//            .toObservable()

    private suspend fun loadFromNetworkAndMaps(): List<DbType> {
        val networkResponse = loadFromNetworks()
        val mappedResponse = mapToDbType(networkResponse)
        saveToDb(mappedResponse)
        return mappedResponse
    }

    protected abstract fun mapToDbType(value: RemoteType): List<DbType>

}