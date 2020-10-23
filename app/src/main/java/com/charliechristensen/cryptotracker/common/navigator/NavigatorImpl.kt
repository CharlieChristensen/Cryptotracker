package com.charliechristensen.cryptotracker.common.navigator

import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import com.charliechristensen.cryptotracker.common.SingleLiveEvent

class NavigatorImpl : Navigator {

    private val pendingNavigationEvent = SingleLiveEvent<NavDirections>()

    override val navigationEvents: LiveData<NavDirections> = pendingNavigationEvent

    override fun navigate(navDirections: NavDirections) {
        pendingNavigationEvent.value = navDirections
    }

}
