package com.charliechristensen.cryptotracker.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collect(
    scope: CoroutineScope,
    crossinline action: suspend (value: T) -> Unit
) {
    scope.launch { collect(action) }
}

inline fun <T, R> Flow<List<T>>.mapItems(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    flow {
        collect { list ->
            emit(list.map { transform(it) })
        }
    }

