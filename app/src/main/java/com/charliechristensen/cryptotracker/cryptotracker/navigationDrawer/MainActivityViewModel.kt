package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import androidx.lifecycle.viewModelScope
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.BaseViewModel
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainActivityViewModel {

    interface Inputs {
    }

    interface Outputs {
        fun theme(): Flow<AppTheme>
        fun getAppThemeSync(): AppTheme
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    class ViewModel @Inject constructor(
        private val liveUpdatePriceClient: LiveUpdatePriceClient,
        private val appPreferences: AppPreferences,
        repository: Repository
    ) : BaseViewModel(), Inputs, Outputs {

        private val themeChannel = BroadcastChannel<AppTheme>(1)

        val inputs: Inputs = this
        val outputs: Outputs = this

        init {
            appPreferences.theme()
                .onEach(themeChannel::send)
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)

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

        override fun getAppThemeSync() = appPreferences.getTheme()

        override fun theme(): Flow<AppTheme> = themeChannel.asFlow()

        //endregion

    }

}
