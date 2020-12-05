package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.navigation.NavDirections
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import kotlinx.coroutines.flow.Flow

interface MainActivityViewModel {

    interface Inputs

    interface Outputs {
        val navigationEvents: Flow<NavDirections>
    }

    class ViewModel constructor(
        private val liveUpdatePriceClient: LiveUpdatePriceClient,
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

        override val navigationEvents: Flow<NavDirections> = navigator.navigationEvents

        //endregion
    }
}
