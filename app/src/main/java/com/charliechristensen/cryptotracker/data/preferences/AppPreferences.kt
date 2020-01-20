package com.charliechristensen.cryptotracker.data.preferences

import com.charliechristensen.cryptotracker.common.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface for saving and loading simple application prefs
 */
interface AppPreferences {

    fun setLiveUpdatePrices(shouldUpdatePrices: Boolean)
    fun setTheme(theme: AppTheme)

    fun theme(): Flow<AppTheme>

    fun getLiveUpdatePrices(): Boolean
    fun getTheme(): AppTheme
    fun liveUpdatePrices(): Flow<Boolean>
    fun setCurrency(symbol: String)
    fun getCurrency(): String
    fun currency(): Flow<String>
}
