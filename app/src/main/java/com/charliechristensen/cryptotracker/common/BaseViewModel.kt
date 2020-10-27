package com.charliechristensen.cryptotracker.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

abstract class BaseViewModel : ViewModel() {

    fun <T> Flow<T>.share(): Flow<T> = this.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

}
