package com.charliechristensen.cryptotracker.data.preferences

import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AppPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    AppPreferences {

    private val themeChannel = ConflatedBroadcastChannel<AppTheme>()
    private val liveUpdatePricesChannel = ConflatedBroadcastChannel<Boolean>()

    init {
        val theme = getTheme()
        themeChannel.offer(theme)
        val liveUpdatePrices = getLiveUpdatePrices()
        liveUpdatePricesChannel.offer(liveUpdatePrices)
    }

    override fun setLiveUpdatePrices(shouldUpdatePrices: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_LIVE_UPDATE_PRICES, shouldUpdatePrices)
            .apply()
        liveUpdatePricesChannel.offer(shouldUpdatePrices)
    }

    override fun getLiveUpdatePrices() =
        sharedPreferences.getBoolean(KEY_LIVE_UPDATE_PRICES, true)

    override fun setTheme(theme: AppTheme) {
        sharedPreferences.edit()
            .putInt(KEY_APP_THEME, theme.restoreId)
            .apply()
        themeChannel.offer(theme)
    }

    override fun getTheme(): AppTheme {
        val restoreId = sharedPreferences.getInt(KEY_APP_THEME, 0)
        return AppTheme.themeFromRestoreId(
            restoreId
        )
    }

    override fun theme(): Flow<AppTheme> =
        themeChannel.asFlow()
            .distinctUntilChanged()

    override fun liveUpdatePrices(): Flow<Boolean> =
        liveUpdatePricesChannel.asFlow()
            .distinctUntilChanged()

    companion object {
        const val KEY_LIVE_UPDATE_PRICES = "live_update_prices"
        const val KEY_APP_THEME = "cryptotracker_app_theme"
    }

}
