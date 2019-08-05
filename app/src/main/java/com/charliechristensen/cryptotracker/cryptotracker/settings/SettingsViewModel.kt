package com.charliechristensen.cryptotracker.cryptotracker.settings

import com.charliechristensen.cryptotracker.common.AppPreferencesImpl
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
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
        fun themeDisplay(): Observable<Int>
        fun liveUpdatePrices(): Observable<Boolean>
        fun showChooseThemeDialog(): Observable<Int>
    }

    class ViewModel @Inject constructor(private val appPreferences: AppPreferencesImpl) :
        BaseViewModel(), Inputs, Outputs {

        private val liveUpdatePriceRelay: BehaviorRelay<Boolean> =
            BehaviorRelay.create()
        private val showChooseThemeRelay: PublishRelay<Int> =
            PublishRelay.create()

        val inputs: Inputs = this
        val outputs: Outputs = this

        //region Inputs

        init {
            val liveUpdatePrices = appPreferences.getLiveUpdatePrices()
            liveUpdatePriceRelay.accept(liveUpdatePrices)
        }

        override fun themeButtonClicked() {
            val theme = appPreferences.getTheme()
            showChooseThemeRelay.accept(theme.buttonId)
        }

        override fun liveUpdatePricesToggled(isChecked: Boolean) {
            appPreferences.setLiveUpdatePrices(isChecked)
        }

        override fun themeChosen(checkedButtonId: Int) {
            val theme = AppTheme.themeFromButtonId(checkedButtonId)
            appPreferences.setTheme(theme)
        }

        override fun showChooseThemeDialog(): Observable<Int> =
            showChooseThemeRelay

        //endregion

        //region Outputs

        override fun themeDisplay(): Observable<Int> =
            appPreferences.theme()
                .map { it.displayId }


        override fun liveUpdatePrices(): Observable<Boolean> =
            liveUpdatePriceRelay.distinctUntilChanged()

        //endregion

    }

}