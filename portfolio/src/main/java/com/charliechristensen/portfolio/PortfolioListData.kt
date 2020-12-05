package com.charliechristensen.portfolio

import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString
import com.charliechristensen.cryptotracker.data.models.ui.ValueChangeColor
import com.charliechristensen.portfolio.list.PortfolioListItem

class PortfolioListData(
    val coinList: List<PortfolioListItem> = listOf(),
    val percentChange24Hour: ColorValueString = ColorValueString.empty(),
    val portfolioValueChange: ColorValueString = ColorValueString.empty(),
    val formattedValue: String = ""
)
