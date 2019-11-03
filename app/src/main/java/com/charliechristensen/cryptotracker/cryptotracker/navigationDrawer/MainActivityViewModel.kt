package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
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
        private val appPreferences: AppPreferences,
        repository: Repository
    ) : BaseViewModel(), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            viewModelScope.launch(Dispatchers.IO) {
                repository.refreshCoinListIfNeeded()
            }
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
            .drop(1)
            .asLiveData()

        //endregion

    }

}
