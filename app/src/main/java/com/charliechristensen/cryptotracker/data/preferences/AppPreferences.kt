package com.charliechristensen.cryptotracker.data.preferences

import com.charliechristensen.cryptotracker.common.AppTheme
import io.reactivex.Observable

/**
 * Interface for saving and loading simple application prefs
 */
interface AppPreferences {

    fun setLiveUpdatePrices(shouldUpdatePrices: Boolean)
    fun setTheme(theme: AppTheme)


    fun theme(): Observable<AppTheme>
    fun liveUpdatePrices(): Observable<Boolean>

    fun getLiveUpdatePrices(): Boolean
    fun getTheme(): AppTheme
}