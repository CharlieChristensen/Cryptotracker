package com.charliechristensen.portfolio.list

import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R
import com.charliechristensen.portfolio.databinding.CellAddCoinBinding

class AddCoinCell(
    parent: ViewGroup,
    callback: PortfolioAdapter.PortfolioAdapterCallback
) : BaseViewHolder<PortfolioListItem>(
    inflateView(R.layout.cell_add_coin, parent)
) {

    private val binding = CellAddCoinBinding.bind(itemView).apply {
        this.callback = callback
    }

    override fun bind(listItem: PortfolioListItem) {
        binding.listItem = listItem
    }
}
