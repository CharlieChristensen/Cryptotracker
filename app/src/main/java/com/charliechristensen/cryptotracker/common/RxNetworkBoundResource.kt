package com.charliechristensen.cryptotracker.common

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Exposes observable while deciding when to call network and cache
 */
abstract class RxNetworkBoundResource<DbType, RemoteType> {

    private var isFirstElement: Boolean = true

    val observable: Observable<List<DbType>> by lazy {
        loadFromDb()
            .switchMap {
                if (isFirstElement && shouldFetch(it)) {
                    isFirstElement = false
                    Observable.concat(Observable.just(it), loadFromNetworkAndMap())
                }else {
                    Observable.just(it)
                }
            }
            .distinctUntilChanged()
    }

    protected abstract fun saveToDb(data: List<DbType>)

    protected abstract fun shouldFetch(data: List<DbType>): Boolean

    protected abstract fun loadFromDb(): Observable<List<DbType>>

    protected abstract fun loadFromNetwork(): Single<RemoteType>

    private fun loadFromNetworkAndMap(): Observable<List<DbType>> =
        loadFromNetwork()
            .map { mapToDbType(it) }
            .doOnSuccess { saveToDb(it) }
            .toObservable()

    protected abstract fun mapToDbType(value: RemoteType): List<DbType>

}