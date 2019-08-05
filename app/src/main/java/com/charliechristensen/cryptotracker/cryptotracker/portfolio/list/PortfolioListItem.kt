package com.charliechristensen.cryptotracker.cryptotracker.portfolio.list

import com.charliechristensen.cryptotracker.common.lists.ListItem
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.models.ui.ColorValueString

sealed class PortfolioListItem : ListItem {

    data class Coin(
        val symbol: String,
        val imageUri: String,
        val currentPrice: String,
        val priceChange: ColorValueString,
        val walletValueChange: ColorValueString,
        val walletValue: String
    ) : PortfolioListItem() {
        override val viewType: Int
            get() = R.layout.cell_coin_portfolio

        override fun areItemsTheSame(otherItem: ListItem): Boolean =
            otherItem is Coin && otherItem.symbol == symbol

        override fun areContentsTheSame(otherItem: ListItem): Boolean =
            otherItem == this
    }

    object AddCoin : PortfolioListItem() {
        override val viewType: Int
            get() = R.layout.cell_add_coin

        override fun areItemsTheSame(otherItem: ListItem): Boolean =
            otherItem === this

        override fun areContentsTheSame(otherItem: ListItem): Boolean =
            otherItem == this
    }

}