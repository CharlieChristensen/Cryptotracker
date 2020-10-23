package com.charliechristensen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import com.charliechristensen.cryptotracker.data.Repository
import kotlinx.coroutines.flow.map

/**
 * Settings ViewModel
 */
interface SettingsViewModel {

    interface Inputs {
        fun themeButtonClicked()
        fun currencyButtonClicked()
        fun liveUpdatePricesToggled(isChecked: Boolean)
        fun themeChosen(theme: AppTheme)
        fun setCurrency(symbol: String)
    }

    interface Outputs {
        val themeDisplay: LiveData<Int>
        val liveUpdatePrices: LiveData<Boolean>
        val displayCurrency: LiveData<String>
        val showChooseThemeDialog: LiveData<List<AppTheme>>
        val showCurrencyDialog: LiveData<Array<String>>
    }

    class ViewModel constructor(private val repository: Repository) :
        BaseViewModel(), Inputs, Outputs {

        private val showChooseThemeEvent = SingleLiveEvent<List<AppTheme>>()
        private val showCurrencyDialogEvent = SingleLiveEvent<Array<String>>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun themeButtonClicked() {
            showChooseThemeEvent.value = Constants.availableThemes
        }

        override fun currencyButtonClicked() {
            showCurrencyDialogEvent.value = Constants.availableCurrencies
        }

        override fun liveUpdatePricesToggled(isChecked: Boolean) {
            repository.setLiveUpdatePrices(isChecked)
        }

        override fun themeChosen(theme: AppTheme) {
            repository.setTheme(theme)
        }

        override fun setCurrency(symbol: String) {
            repository.setCurrency(symbol)
        }

        //endregion

        //region Outputs

        override val themeDisplay: LiveData<Int> = repository.theme()
            .map { it.displayId }
            .asLiveData()

        override val liveUpdatePrices: LiveData<Boolean> = repository
            .liveUpdatePrices()
            .asLiveData()

        override val displayCurrency: LiveData<String> = repository
            .currency()
            .asLiveData()

        override val showChooseThemeDialog: LiveData<List<AppTheme>> = showChooseThemeEvent

        override val showCurrencyDialog: LiveData<Array<String>> = showCurrencyDialogEvent

        //endregion

    }
}
