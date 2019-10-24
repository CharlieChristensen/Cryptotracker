package com.charliechristensen.portfolio.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R

class AddCoinCell(
    inflater: LayoutInflater,
    parent: ViewGroup,
    onClickItemCallback: (Int) -> Unit
) : BaseViewHolder<PortfolioListItem>(
    inflater.inflate(R.layout.cell_add_coin, parent, false)
) {

    init {
        itemView.setOnClickListener {
            onClickItemCallback(adapterPosition)
        }
    }

}