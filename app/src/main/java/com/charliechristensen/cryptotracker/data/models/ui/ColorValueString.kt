package com.charliechristensen.cryptotracker.data.models.ui

import java.text.NumberFormat

/**
 * Represents a value with a corresponding color
 *
 * GREEN if the value >= 0
 * RED if the value is < 0
 *
 */
data class ColorValueString constructor(
    val value: String,
    val color: ValueChangeColor
) {

    companion object {
        fun create(
            valueChangeDouble: Double = 0.0,
            formatter: NumberFormat = NumberFormat.getInstance().apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
        ): ColorValueString {
            val color: ValueChangeColor
            val value: String
            when {
                valueChangeDouble >= 0.0 -> {
                    color =
                        ValueChangeColor.GREEN
                    value = "+" + formatter.format(valueChangeDouble)
                }
                else -> {
                    color = ValueChangeColor.RED
                    value = formatter.format(valueChangeDouble) ?: "0.00"
                }
            }
            return ColorValueString(
                value,
                color
            )
        }
    }
}

enum class ValueChangeColor {
    GREEN,
    RED
}
