package com.charliechristensen.cryptotracker.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collect(scope: CoroutineScope, crossinline action: suspend (value: T) -> Unit) {
    scope.launch { collect(action) }
}

