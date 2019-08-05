package com.charliechristensen.cryptotracker.common

import com.charliechristensen.cryptotracker.cryptotracker.R

/**
 * Theme
 */
sealed class AppTheme constructor(
    val styleId: Int,
    val buttonId: Int,
    val displayId: Int,
    val restoreId: Int
) {
    object Dark : AppTheme(R.style.AppTheme, R.id.darkRadioButton, R.string.theme_dark, 0)
    object Light : AppTheme(R.style.AppTheme_Light, R.id.lightRadioButton, R.string.theme_light, 1)
    object Teal : AppTheme(R.style.AppTheme_Teal, R.id.tealRadioButton, R.string.teal, 2)

    companion object {
        fun themeFromButtonId(buttonId: Int): AppTheme = when (buttonId) {
            Light.buttonId -> Light
            Teal.buttonId -> Teal
            else -> Dark
        }

        fun themeFromRestoreId(restoreId: Int): AppTheme = when (restoreId) {
            1 -> Light
            2 -> Teal
            else -> Dark
        }
    }

}