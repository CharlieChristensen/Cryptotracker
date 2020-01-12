package com.charliechristensen.cryptotracker.common.navigator

import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import com.charliechristensen.cryptotracker.common.SingleLiveEvent
import javax.inject.Inject

class NavigatorImpl @Inject constructor(): Navigator {

    private val pendingNavigationEvent = SingleLiveEvent<NavDirections>()

    override val navigationEvents: LiveData<NavDirections> = pendingNavigationEvent

    override fun navigate(navDirections: NavDirections) {
        pendingNavigationEvent.value = navDirections
    }

}
