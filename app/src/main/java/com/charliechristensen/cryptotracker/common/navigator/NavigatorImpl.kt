package com.charliechristensen.cryptotracker.common.navigator

import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class NavigatorImpl : Navigator {

    private val pendingNavigationEvent = MutableSharedFlow<NavDirections>(1)

    override val navigationEvents: Flow<NavDirections> = pendingNavigationEvent

    override fun navigate(navDirections: NavDirections) {
        pendingNavigationEvent.tryEmit(navDirections)
    }

}
