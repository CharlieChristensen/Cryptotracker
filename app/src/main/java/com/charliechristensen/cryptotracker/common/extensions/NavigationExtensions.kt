package com.charliechristensen.cryptotracker.common.extensions

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.charliechristensen.cryptotracker.cryptotracker.R

fun NavController.navigateRight(
    deepLink: Uri
) {
    navigate(
        deepLink,
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_enter_from_right)
            .setExitAnim(R.anim.slide_exit_to_left)
            .setPopEnterAnim(R.anim.slide_enter_from_left)
            .setPopExitAnim(R.anim.slide_exit_to_right)
            .build()
    )
}
