package com.charliechristensen.cryptotracker.common

import androidx.annotation.AttrRes
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor

/**
 * Created by Chuck on 1/4/2018.
 */
class ColorUtils {
    companion object {
        @AttrRes
        fun getColorInt(percentChangeColor: ValueChangeColor): Int = when (percentChangeColor) {
            ValueChangeColor.GREEN -> R.attr.positivePriceGreen
            ValueChangeColor.RED -> R.attr.negativePriceRed
        }
    }
}
