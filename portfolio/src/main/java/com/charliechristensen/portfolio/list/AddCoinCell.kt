package com.charliechristensen.portfolio.list

import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.databinding.CellAddCoinBinding

class AddCoinCell(
    private val binding: CellAddCoinBinding,
    private val callback: PortfolioAdapter.PortfolioAdapterCallback
) : BaseViewHolder<PortfolioListItem>(binding.root) {
    override fun bind(listItem: PortfolioListItem) {
        binding.apply {
            root.setOnClickListener { callback.onClickItem(listItem) }

        }
    }
}
