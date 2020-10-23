package com.charliechristensen.cryptotracker.common.navigator

import androidx.navigation.NavDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class NavigatorImpl : Navigator {

    private val scope = CoroutineScope(Job())

    private val pendingNavigationEvent = MutableSharedFlow<NavDirections>()

    override val navigationEvents: Flow<NavDirections> = pendingNavigationEvent

    override fun navigate(navDirections: NavDirections) {
        scope.launch {
            pendingNavigationEvent.emit(navDirections)
        }
    }

}
