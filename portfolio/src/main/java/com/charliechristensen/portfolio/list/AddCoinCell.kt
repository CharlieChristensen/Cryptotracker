package com.charliechristensen.portfolio.list

import android.view.View
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R
import kotlinx.android.extensions.LayoutContainer

class AddCoinCell(
    parent: ViewGroup,
    private val onClickItemCallback: (PortfolioListItem) -> Unit
) : BaseViewHolder<PortfolioListItem>(
    inflateView(R.layout.cell_add_coin, parent)
), LayoutContainer {

    override val containerView: View = itemView

    override fun bind(listItem: PortfolioListItem) {
        itemView.setOnClickListener {
            onClickItemCallback(listItem)
        }
    }
}
