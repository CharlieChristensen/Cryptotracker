package com.charliechristensen.cryptotracker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

fun <T : Any> DataStore<Preferences>.getFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> = data
    .map { preferences -> preferences[key] ?: defaultValue }

suspend fun <T : Any> DataStore<Preferences>.get(key: Preferences.Key<T>, defaultValue: T): T = withContext(Dispatchers.IO) {
    data.firstOrNull()?.get(key) ?: defaultValue
}

suspend fun <T> DataStore<Preferences>.updateValue(key: Preferences.Key<T>, value: T) {
    edit { preferences ->
        preferences[key] = value
    }
}
