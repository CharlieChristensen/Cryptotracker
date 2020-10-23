package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface MainActivityViewModel {

    interface Inputs

    interface Outputs {
        val theme: LiveData<AppTheme>
        val navigationEvents: Flow<NavDirections>
        fun getAppThemeSync(): AppTheme
    }

    @ExperimentalCoroutinesApi
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

        override val theme: LiveData<AppTheme> = repository.theme()
            .asLiveData()

        override val navigationEvents: Flow<NavDirections> = navigator.navigationEvents

        override fun getAppThemeSync(): AppTheme =
            repository.getTheme()

        //endregion
    }
}
