package com.charliechristensen.cryptotracker.common.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.charliechristensen.cryptotracker.cryptotracker.R


fun NavController.navigateRight(
    navDirections: NavDirections
) {
    navigate(
        navDirections,
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_enter_from_right)
            .setExitAnim(R.anim.slide_exit_to_left)
            .setPopEnterAnim(R.anim.slide_enter_from_left)
            .setPopExitAnim(R.anim.slide_exit_to_right)
            .build()
    )
}
