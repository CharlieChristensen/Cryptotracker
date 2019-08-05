package com.charliechristensen.cryptotracker.common

import android.content.SharedPreferences
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Saves preferences to SharedPreferences
 *
 * I feel that this is a better solution for simple data than Room.
 */
@Singleton
class AppPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) : AppPreferences {

    private val themeRelay: BehaviorRelay<AppTheme> = BehaviorRelay.create()
    private val liveUpdatePricesRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    init {
        val theme = getTheme()
        themeRelay.accept(theme)
        val liveUpdatePrices = getLiveUpdatePrices()
        liveUpdatePricesRelay.accept(liveUpdatePrices)
    }

    override fun setLiveUpdatePrices(shouldUpdatePrices: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_LIVE_UPDATE_PRICES, shouldUpdatePrices)
        editor.apply()
        liveUpdatePricesRelay.accept(shouldUpdatePrices)
    }

    override fun getLiveUpdatePrices() =
        sharedPreferences.getBoolean(KEY_LIVE_UPDATE_PRICES, true)

    override fun setTheme(theme: AppTheme) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_APP_THEME, theme.restoreId)
        editor.apply()
        themeRelay.accept(theme)
    }

    override fun getTheme(): AppTheme {
        val restoreId = sharedPreferences.getInt(KEY_APP_THEME, 0)
        return AppTheme.themeFromRestoreId(restoreId)
    }

    override fun theme(): Observable<AppTheme> =
        themeRelay.distinctUntilChanged()

    override fun liveUpdatePrices(): Observable<Boolean> =
        liveUpdatePricesRelay.distinctUntilChanged()

    companion object {
        const val KEY_LIVE_UPDATE_PRICES = "live_update_prices"
        const val KEY_APP_THEME = "cryptotracker_app_theme"
    }

}