package com.charliechristensen.cryptotracker.cryptotracker.coinList.list

import com.charliechristensen.cryptotracker.common.lists.ListItem
import com.charliechristensen.cryptotracker.cryptotracker.R

sealed class SearchCoinsListItem : ListItem {

    data class Coin(
        val name: String,
        val symbol: String,
        val imageUri: String
    ) : SearchCoinsListItem() {
        override val viewType: Int
            get() = R.layout.cell_coin_list

        override fun areItemsTheSame(otherItem: ListItem): Boolean =
            otherItem is Coin && otherItem.symbol == symbol

        override fun areContentsTheSame(otherItem: ListItem): Boolean =
            otherItem == this
    }

    object Loading : SearchCoinsListItem() {
        override val viewType: Int
            get() = R.layout.view_loading_header

        override fun areItemsTheSame(otherItem: ListItem): Boolean =
            otherItem === this

        override fun areContentsTheSame(otherItem: ListItem): Boolean =
            otherItem == this
    }

    object RefreshFooter : SearchCoinsListItem() {
        override val viewType: Int
            get() = R.layout.cell_coin_list_footer

        override fun areItemsTheSame(otherItem: ListItem): Boolean =
            otherItem === this

        override fun areContentsTheSame(otherItem: ListItem): Boolean =
            otherItem == this
    }

}