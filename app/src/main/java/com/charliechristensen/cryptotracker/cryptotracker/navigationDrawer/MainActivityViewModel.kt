package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

interface MainActivityViewModel {

    interface Inputs

    interface Outputs {
        val theme: Flow<AppTheme>
        val navigationEvents: Flow<NavDirections>
        fun getAppThemeSync(): AppTheme
    }

    class ViewModel constructor(
        private val liveUpdatePriceClient: LiveUpdatePriceClient,
        private val repository: Repository,
        navigator: Navigator
    ) : BaseViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            liveUpdatePriceClient.start()
        }

        override fun onCleared() {
            super.onCleared()
            liveUpdatePriceClient.stop()
        }

        //region Inputs

        //endregion

        //region Outputs

        override val theme: Flow<AppTheme> = repository
            .theme()
            .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

        override val navigationEvents: Flow<NavDirections> = navigator.navigationEvents

        override fun getAppThemeSync(): AppTheme =
            repository.getTheme()

        //endregion
    }
}
