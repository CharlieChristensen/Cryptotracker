package com.charliechristensen.cryptotracker.common.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

inline fun <T, R> Flow<List<T>>.mapItems(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    flow {
        collect { list ->
            emit(list.map { transform(it) })
        }
    }
