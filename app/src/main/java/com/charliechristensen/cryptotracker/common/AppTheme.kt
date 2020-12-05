package com.charliechristensen.cryptotracker.common

import androidx.appcompat.app.AppCompatDelegate
import com.charliechristensen.cryptotracker.cryptotracker.R

/**
 * Theme
 */
sealed class AppTheme constructor(
    val styleId: Int,
    val displayId: Int,
    val restoreId: Int
) {
    object Dark : AppTheme(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark, 0)
    object Teal : AppTheme(AppCompatDelegate.MODE_NIGHT_NO, R.string.teal, 2)

    companion object {

        fun themeFromRestoreId(restoreId: Int): AppTheme = when (restoreId) {
            Teal.restoreId -> Teal
            else -> Dark
        }

    }
}
