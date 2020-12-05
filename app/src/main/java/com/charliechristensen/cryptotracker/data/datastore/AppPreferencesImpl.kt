package com.charliechristensen.cryptotracker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AppPreferencesImpl constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferences {

    override suspend fun setLiveUpdatePrices(shouldUpdatePrices: Boolean) {
        dataStore.updateValue(KEY_LIVE_UPDATE_PRICES, shouldUpdatePrices)
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.updateValue(KEY_APP_THEME, theme.restoreId)
    }

    override fun theme(): Flow<AppTheme> =
        dataStore.getFlow(KEY_APP_THEME, 0)
            .map(AppTheme::themeFromRestoreId)
            .distinctUntilChanged()

    override fun liveUpdatePrices(): Flow<Boolean> =
        dataStore.getFlow(KEY_LIVE_UPDATE_PRICES, true)

    override suspend fun setCurrency(symbol: String) {
        dataStore.updateValue(KEY_DISPLAY_CURRENCY, symbol)
    }

    override suspend fun getCurrency(): String =
        dataStore.get(KEY_DISPLAY_CURRENCY, Constants.DefaultCurrency)

    override fun currency(): Flow<String> =
        dataStore.getFlow(KEY_DISPLAY_CURRENCY, Constants.DefaultCurrency) //TODO move common constants and such to common module

    companion object {
        private val KEY_LIVE_UPDATE_PRICES = preferencesKey<Boolean>("live_update_prices")
        private val KEY_APP_THEME = preferencesKey<Int>("cryptotracker_app_theme")
        private val KEY_DISPLAY_CURRENCY = preferencesKey<String>("key_display_currency")
    }
}
