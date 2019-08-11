package com.charliechristensen.cryptotracker.data.preferences

import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.AppTheme
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    AppPreferences {

    private val themeRelay: BehaviorRelay<AppTheme> = BehaviorRelay.create()
    private val liveUpdatePricesRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()

    init {
        val theme = getTheme()
        themeRelay.accept(theme)
        val liveUpdatePrices = getLiveUpdatePrices()
        liveUpdatePricesRelay.accept(liveUpdatePrices)
    }

    override fun setLiveUpdatePrices(shouldUpdatePrices: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_LIVE_UPDATE_PRICES, shouldUpdatePrices)
            .apply()
        liveUpdatePricesRelay.accept(shouldUpdatePrices)
    }

    override fun getLiveUpdatePrices() =
        sharedPreferences.getBoolean(KEY_LIVE_UPDATE_PRICES, true)

    override fun setTheme(theme: AppTheme) {
        sharedPreferences.edit()
            .putInt(KEY_APP_THEME, theme.restoreId)
            .apply()
        themeRelay.accept(theme)
    }

    override fun getTheme(): AppTheme {
        val restoreId = sharedPreferences.getInt(KEY_APP_THEME, 0)
        return AppTheme.themeFromRestoreId(
            restoreId
        )
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