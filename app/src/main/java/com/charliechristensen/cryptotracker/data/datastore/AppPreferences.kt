package com.charliechristensen.cryptotracker.data.datastore

import com.charliechristensen.cryptotracker.common.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface for saving and loading simple application prefs
 */
interface AppPreferences {

    suspend fun setLiveUpdatePrices(shouldUpdatePrices: Boolean)
    suspend fun setTheme(theme: AppTheme)

    fun theme(): Flow<AppTheme>

    fun liveUpdatePrices(): Flow<Boolean>
    suspend fun setCurrency(symbol: String)
    suspend fun getCurrency(): String
    fun currency(): Flow<String>

}
