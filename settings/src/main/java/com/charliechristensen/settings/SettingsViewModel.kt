package com.charliechristensen.settings

import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.AppTheme.*
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Settings ViewModel
 */
interface SettingsViewModel {

    interface Inputs {
        fun themeButtonClicked()
        fun liveUpdatePricesToggled(isChecked: Boolean)
        fun themeChosen(checkedButtonId: Int)
    }

    interface Outputs {
        fun themeDisplay(): Flow<Int>
        fun liveUpdatePrices(): Flow<Boolean>
        fun showChooseThemeDialog(): Flow<Int>
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @Inject constructor(private val appPreferences: AppPreferences) :
        BaseViewModel(), Inputs, Outputs {

        private val liveUpdatePriceChannel = ConflatedBroadcastChannel(appPreferences.getLiveUpdatePrices())
        private val showChooseThemeChannel = BroadcastChannel<AppTheme>(1)

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun themeButtonClicked() {
            showChooseThemeChannel.offer(appPreferences.getTheme())
        }

        override fun liveUpdatePricesToggled(isChecked: Boolean) {
            appPreferences.setLiveUpdatePrices(isChecked)
        }

        override fun themeChosen(checkedButtonId: Int) {
            val theme = themeFromButtonId(checkedButtonId)
            appPreferences.setTheme(theme)
        }

        //endregion

        //region Outputs

        override fun themeDisplay(): Flow<Int> =
            appPreferences.theme()
                .map { it.displayId }

        override fun liveUpdatePrices(): Flow<Boolean> =
            liveUpdatePriceChannel.asFlow()
                .distinctUntilChanged()

        override fun showChooseThemeDialog(): Flow<Int> =
            showChooseThemeChannel.asFlow()
                .map { buttonIdFromTheme(it) }

        //endregion

        private fun themeFromButtonId(buttonId: Int): AppTheme = when (buttonId) {
            R.id.lightRadioButton -> Light
            R.id.tealRadioButton -> Teal
            R.id.darkRadioButton -> Dark
            else -> error("Unknown app theme")
        }

        private fun buttonIdFromTheme(appTheme: AppTheme) = when (appTheme) {
            Light -> R.id.lightRadioButton
            Teal -> R.id.tealRadioButton
            Dark -> R.id.darkRadioButton
        }

    }

}
