package com.charliechristensen.cryptotracker.common.navigator

import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.Flow

interface Navigator {

    val navigationEvents: Flow<NavDirections>

    fun navigate(navDirections: NavDirections)

}
