package com.charliechristensen.cryptotracker.common.navigator

import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections

interface Navigator {

    val navigationEvents: LiveData<NavDirections>

    fun navigate(navDirections: NavDirections)

}
