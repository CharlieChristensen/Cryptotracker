package com.charliechristensen.cryptotracker.common.extensions

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

inline fun <T, R> Flow<List<T>>.mapItems(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    flow {
        collect { list ->
            emit(list.map { transform(it) })
        }
    }

fun <T : Any> Query<T>.flowAsList(): Flow<List<T>> = asFlow().mapToList()
