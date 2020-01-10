package com.charliechristensen.portfolio.list

import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseListAdapter
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R

class PortfolioAdapter(private val onClickItemCallback: PortfolioAdapterCallback) : BaseListAdapter<PortfolioListItem>() {

    interface PortfolioAdapterCallback {
        fun onClickItem(listItem: PortfolioListItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<PortfolioListItem> = when (viewType) {
        R.layout.cell_coin_portfolio -> PortfolioCoinCell(parent, onClickItemCallback)
        R.layout.cell_add_coin -> AddCoinCell(parent, onClickItemCallback)
        else -> error("No view for viewType:$viewType exists for ${PortfolioAdapter::class.java.simpleName}")
    }
}
