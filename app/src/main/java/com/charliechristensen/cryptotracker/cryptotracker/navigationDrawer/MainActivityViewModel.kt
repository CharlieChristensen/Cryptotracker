package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop
import javax.inject.Inject

interface MainActivityViewModel {

    interface Inputs {
    }

    interface Outputs {
        val theme: LiveData<AppTheme>
        fun getAppThemeSync(): AppTheme
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    class ViewModel @Inject constructor(
        private val liveUpdatePriceClient: LiveUpdatePriceClient,
        private val appPreferences: AppPreferences
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

        override fun getAppThemeSync(): AppTheme =
            appPreferences.getTheme()

        override val theme: LiveData<AppTheme> = appPreferences.theme()
            .asLiveData()

        //endregion

    }

}
