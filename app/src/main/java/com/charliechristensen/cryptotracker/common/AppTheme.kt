package com.charliechristensen.cryptotracker.common

import com.charliechristensen.cryptotracker.cryptotracker.R

/**
 * Theme
 */
sealed class AppTheme constructor(
    val styleId: Int,
    val displayId: Int,
    val restoreId: Int
) {
    object Dark : AppTheme(R.style.AppTheme_Dark, R.string.theme_dark, 0)
    object Teal : AppTheme(R.style.AppTheme_Teal, R.string.teal, 2)

    companion object {

        fun themeFromRestoreId(restoreId: Int): AppTheme = when (restoreId) {
            2 -> Teal
            else -> Dark
        }

    }
}
