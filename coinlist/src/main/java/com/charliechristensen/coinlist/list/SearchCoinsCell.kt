package com.charliechristensen.coinlist.list

import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.coinlist.databinding.CellCoinListBinding
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsCell(
    parent: ViewGroup,
    callback: SearchCoinsAdapter.SearchCoinAdapterCallback
) : BaseViewHolder<SearchCoinsListItem>(inflateView(R.layout.cell_coin_list, parent)) {

    private val binding = CellCoinListBinding.bind(itemView).apply {
        this.callback = callback
    }

    override fun bind(listItem: SearchCoinsListItem) {
        if (listItem is SearchCoinsListItem.Coin) {
            binding.coin = listItem
            binding.executePendingBindings()
        }
    }
}
