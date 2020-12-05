package com.charliechristensen.settings

import androidx.lifecycle.viewModelScope
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
        val themeDisplay: Flow<Int>
        val liveUpdatePrices: Flow<Boolean>
        val displayCurrency: Flow<String>
        val showChooseThemeDialog: Flow<List<AppTheme>>
        val showCurrencyDialog: Flow<Array<String>>
    }

    class ViewModel constructor(private val repository: Repository) :
        BaseViewModel(), Inputs, Outputs {

        private val showChooseThemeEvent = MutableSharedFlow<List<AppTheme>>()
        private val showCurrencyDialogEvent = MutableSharedFlow<Array<String>>()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        override fun themeButtonClicked() {
            viewModelScope.launch {
                showChooseThemeEvent.emit(Constants.availableThemes)
            }
        }

        override fun currencyButtonClicked() {
            viewModelScope.launch {
                showCurrencyDialogEvent.emit(Constants.availableCurrencies)
            }
        }

        override fun liveUpdatePricesToggled(isChecked: Boolean) {
            viewModelScope.launch {
                repository.setLiveUpdatePrices(isChecked)
            }
        }

        override fun themeChosen(theme: AppTheme) {
            viewModelScope.launch {
                repository.setTheme(theme)
            }
        }

        override fun setCurrency(symbol: String) {
            viewModelScope.launch {
                repository.setCurrency(symbol)
            }
        }

        //endregion

        //region Outputs

        override val themeDisplay: Flow<Int> = repository
            .theme()
            .map { it.displayId }
            .share()

        override val liveUpdatePrices: Flow<Boolean> = repository
            .liveUpdatePrices()
            .share()

        override val displayCurrency: Flow<String> = repository
            .currency()
            .share()

        override val showChooseThemeDialog: Flow<List<AppTheme>> = showChooseThemeEvent

        override val showCurrencyDialog: Flow<Array<String>> = showCurrencyDialogEvent

        //endregion

    }
}
