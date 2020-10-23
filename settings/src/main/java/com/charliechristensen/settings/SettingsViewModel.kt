package com.charliechristensen.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
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
        val themeDisplay: LiveData<Int>
        val liveUpdatePrices: LiveData<Boolean>
        val displayCurrency: LiveData<String>
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

        override val themeDisplay: LiveData<Int> = repository
            .theme()
            .map { it.displayId }
            .asLiveData()

        override val liveUpdatePrices: LiveData<Boolean> = repository
            .liveUpdatePrices()
            .asLiveData()

        override val displayCurrency: LiveData<String> = repository
            .currency()
            .asLiveData()

        override val showChooseThemeDialog: Flow<List<AppTheme>> = showChooseThemeEvent

        override val showCurrencyDialog: Flow<Array<String>> = showCurrencyDialogEvent

        //endregion

    }
}
