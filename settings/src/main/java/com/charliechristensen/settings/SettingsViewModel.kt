package com.charliechristensen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.AppTheme.*
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
        val themeDisplay: LiveData<Int>
        val liveUpdatePrices: LiveData<Boolean>
        val showChooseThemeDialog: LiveData<Int>
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    class ViewModel @Inject constructor(private val appPreferences: AppPreferences) :
        BaseViewModel(), Inputs, Outputs {

        private val showChooseThemeChannel = SingleLiveEvent<AppTheme>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun themeButtonClicked() {
            showChooseThemeChannel.value = appPreferences.getTheme()
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

        override val showChooseThemeDialog: LiveData<Int> =
            showChooseThemeChannel.map { buttonIdFromTheme(it) }

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
