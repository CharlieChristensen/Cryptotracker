package com.charliechristensen.portfolio.list

import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R
import com.charliechristensen.portfolio.databinding.CellCoinPortfolioBinding

class PortfolioCoinCell(
    parent: ViewGroup,
    callback: PortfolioAdapter.PortfolioAdapterCallback
) : BaseViewHolder<PortfolioListItem>(
    inflateView(R.layout.cell_coin_portfolio, parent)
) {

    private val binding = CellCoinPortfolioBinding.bind(itemView).apply {
        this.callback = callback
    }

    override fun bind(listItem: PortfolioListItem) {
        if (listItem is PortfolioListItem.Coin) {
            binding.listItem = listItem
        }
    }
}
