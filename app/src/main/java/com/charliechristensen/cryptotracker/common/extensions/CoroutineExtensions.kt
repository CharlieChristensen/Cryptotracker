package com.charliechristensen.cryptotracker.common.extensions

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

inline fun <T : Any> Query<T>.flowAsList(): Flow<List<T>> = asFlow().mapToList()

inline fun <T> Flow<T>.accumulate(
    initial: T
): Flow<Pair<T, T>> = flow {
    var previous: T = initial
    emit(previous to previous)
    collect { next ->
        emit(previous to next)
        previous = next
    }
}
