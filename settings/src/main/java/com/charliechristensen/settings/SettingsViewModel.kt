package com.charliechristensen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.AppTheme.Dark
import com.charliechristensen.cryptotracker.common.AppTheme.Teal
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.map

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
        val themeDisplay: LiveData<Int>
        val liveUpdatePrices: LiveData<Boolean>
        val showChooseThemeDialog: LiveData<Int>
    }

    class ViewModel @Inject constructor(private val appPreferences: AppPreferences) :
        BaseViewModel(), Inputs, Outputs {

        private val showChooseThemeChannel = SingleLiveEvent<Int>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun themeButtonClicked() {
            showChooseThemeChannel.value = buttonIdFromTheme(appPreferences.getTheme())
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

        override val themeDisplay: LiveData<Int> = appPreferences.theme()
            .map { it.displayId }
            .asLiveData()

        override val liveUpdatePrices: LiveData<Boolean> =
            MutableLiveData(appPreferences.getLiveUpdatePrices())

        override val showChooseThemeDialog: LiveData<Int> = showChooseThemeChannel

        //endregion

        private fun themeFromButtonId(buttonId: Int): AppTheme = when (buttonId) {
            R.id.tealRadioButton -> Teal
            R.id.darkRadioButton -> Dark
            else -> error("Unknown app theme")
        }

        private fun buttonIdFromTheme(appTheme: AppTheme) = when (appTheme) {
            Teal -> R.id.tealRadioButton
            Dark -> R.id.darkRadioButton
        }
    }
}
