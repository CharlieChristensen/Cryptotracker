package com.charliechristensen.portfolio.list

import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R

class AddCoinCell(
    parent: ViewGroup,
    private val onClickItemCallback: (PortfolioListItem) -> Unit
) : BaseViewHolder<PortfolioListItem>(
    inflateView(R.layout.cell_add_coin, parent)
) {

    override fun bind(listItem: PortfolioListItem) {
        itemView.setOnClickListener {
            onClickItemCallback(listItem)
        }
    }
}
