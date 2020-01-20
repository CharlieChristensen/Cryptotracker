package com.charliechristensen.cryptotracker.data.preferences

import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.Constants
import com.tfcporciuncula.flow.FlowSharedPreferences
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class AppPreferencesImpl @Inject constructor(sharedPreferences: SharedPreferences) :
    AppPreferences {

    private val flowSharedPreferences = FlowSharedPreferences(sharedPreferences)

    private val liveUpdatePricesPref =
        flowSharedPreferences.getBoolean(KEY_LIVE_UPDATE_PRICES, true)
    private val themePref = flowSharedPreferences.getInt(KEY_APP_THEME, 0)
    private val selectedCurrencyPref =
        flowSharedPreferences.getString(KEY_DISPLAY_CURRENCY, Constants.DefaultCurrency)

    override fun setLiveUpdatePrices(shouldUpdatePrices: Boolean) {
        liveUpdatePricesPref.set(shouldUpdatePrices)
    }

    override fun getLiveUpdatePrices(): Boolean =
        liveUpdatePricesPref.get()

    override fun setTheme(theme: AppTheme) {
        themePref.set(theme.restoreId)
    }

    override fun getTheme(): AppTheme =
        AppTheme.themeFromRestoreId(themePref.get())

    override fun theme(): Flow<AppTheme> =
        themePref.asFlow()
            .map { AppTheme.themeFromRestoreId(it) }
            .distinctUntilChanged()

    override fun liveUpdatePrices(): Flow<Boolean> =
        liveUpdatePricesPref.asFlow()
            .distinctUntilChanged()

    override fun setCurrency(symbol: String) {
        selectedCurrencyPref.set(symbol)
    }

    override fun getCurrency(): String =
        selectedCurrencyPref.get()

    override fun currency(): Flow<String> =
        selectedCurrencyPref.asFlow()

    companion object {
        const val KEY_LIVE_UPDATE_PRICES = "live_update_prices"
        const val KEY_APP_THEME = "cryptotracker_app_theme"
        const val KEY_DISPLAY_CURRENCY = "key_display_currency"
    }
}
