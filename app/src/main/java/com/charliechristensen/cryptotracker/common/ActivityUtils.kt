package com.charliechristensen.cryptotracker.common

import android.app.Activity
import android.util.TypedValue

/**
 * Created by Chuck on 1/8/2018.
 */

fun Activity.getColorFromResource(colorAttribute: Int): Int{
    val typedValue = TypedValue()
    val theme = theme
    theme.resolveAttribute(colorAttribute, typedValue, true)
    return typedValue.data
}
